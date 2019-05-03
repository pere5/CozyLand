package main.thread

import main.Model
import main.Model.TravelType
import main.Node
import main.exception.PerIsBorkenException
import main.villager.StraightPath
import main.villager.Villager

class PathfinderWorker extends Worker {

    /*
        - [ ] kör steg nod för nod
        - [ ] för nästa steg:
          - [x] kör 90 grader med mittersta graden pekandes mot målet
          - [x] fördela ut graderna lika till grann noderna
          - [x] hårdkoda det med en färdig lösning per grad för alla 360 grader.
          - [x] lägg upp en (normal) fördelning av sannolikhet för graderna över 90 grader.
          - [x] hårdkoda fördelningen för 0 -> 90 grader med max i 45
          - [x] beräkna genomsnittliga sannolikheten för varje grann nod relativt till de andra noderna utifrån gradernas sannolikheter
          - [x] omfördela sannolikheterna mot vaje nod baserat på nodens movementCost relativt till de andra noderna
     */

    def update() {

        for (Villager villager: Model.model.villagers) {
            if (villager.pathfinderWorker) {

                def visitedSquares = [:]
                def exhaustedSquares = [:]

                def start = [villager.x, villager.y] as Double[]
                def dest = Model.generateXY()

                def realSquareProbabilities = realSquareProbabilities(villager, Model.round(start), Model.round(dest))


                villager.actionQueue << new StraightPath(start, dest)
                villager.toWorkWorker()
            }
        }
    }

    def realSquareProbabilities(Villager villager, int[] start, int[] dest) {

        def realSquareProbabilities = [:]

        def realDegree = Model.calculateDegree(start, dest)
        def (int x, int y) = Model.pixelToNodeIdx(start)
        def nodeNetwork = Model.model.nodeNetwork as Node[][]
        def node = nodeNetwork[x][y]

        final def SQUARE_PROBABILITIES = Model.model.squareProbabilitiesForDegrees[realDegree]

        SQUARE_PROBABILITIES.each { def SQUARE ->
            def (int sX, int sY) = SQUARE[0]
            def nX = x + sX
            def nY = y + sY
            def squareProbability = SQUARE[1] as Double
            def neighbor = nodeNetwork[nX][nY] as Node
            def travelModifierMap = Model.model.travelModifier as Map<TravelType, Double>

            TravelType travelType = neighbor.travelType
            if (villager.canTravel(travelType)) {
                int heightDifference = neighbor.height - node.height
                Double travelModifier = travelModifierMap[travelType]
                Double heightModifier = (heightDifference > 0
                        ? travelModifierMap[TravelType.UP_HILL]
                        : heightDifference == 0
                        ? travelModifierMap[TravelType.EVEN]
                        : travelModifierMap[TravelType.DOWN_HILL]) as Double
                Double probability = (1 / (heightModifier * travelModifier)) * squareProbability
                realSquareProbabilities[SQUARE[0]] = probability
            } else {
                realSquareProbabilities[SQUARE[0]] = 0d
            }
        }

        //we need to start looking at visited squares

        def sum = realSquareProbabilities.collect{ it.value }.sum() as Double

        if (sum == 0) {
            SQUARE_PROBABILITIES.each { def SQUARE ->
                def (int sX, int sY) = SQUARE[0]
                def nX = x + sX
                def nY = y + sY
                def neighbor = nodeNetwork[nX][nY] as Node
                TravelType travelType = neighbor.travelType
                if (villager.canTravel(travelType)) {
                    realSquareProbabilities[SQUARE[0]] = 1d
                }
            }
        }

        sum = realSquareProbabilities.collect{ it.value }.sum() as Double

        if (sum == 0) {
            throw new PerIsBorkenException()
        }

        def globalModifier = 100 / sum
        realSquareProbabilities.each {
            it.value *= globalModifier
        }

        sum = realSquareProbabilities.collect{ it.value }.sum() as Double

        if (sum - 100 > 0.00000001) {
            throw new PerIsBorkenException()
        }

        return realSquareProbabilities
    }
}
