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

                def nextSquares = nextSquares(villager, Model.round(start), Model.round(dest), visitedSquares)

                def random = Math.random() * 100

                //def step = nextSquares[]


                villager.actionQueue << new StraightPath(start, dest)
                villager.toWorkWorker()
            }
        }
    }

    def nextSquares(Villager villager, int[] start, int[] dest, Map visitedSquares) {

        def nextSquares = []

        def realDegree = Model.calculateDegree(start, dest)
        def (int x, int y) = Model.pixelToNodeIdx(start)
        def nodeNetwork = Model.model.nodeNetwork as Node[][]
        def node = nodeNetwork[x][y]

        final def SQUARE_PROBABILITIES = Model.model.squareProbabilitiesForDegrees[realDegree]

        SQUARE_PROBABILITIES.each { def SQUARE ->
            def (int sX, int sY) = SQUARE[0]
            def nX = x + sX
            def nY = y + sY
            def neighbor = nodeNetwork[nX][nY] as Node
            TravelType travelType = neighbor.travelType
            def squareProbability = SQUARE[1] as Double

            if (villager.canTravel(travelType) && squareProbability > 0) {
                def travelModifierMap = Model.model.travelModifier as Map<TravelType, Double>
                int heightDifference = neighbor.height - node.height
                Double travelModifier = travelModifierMap[travelType]
                Double heightModifier = (heightDifference > 0
                        ? travelModifierMap[TravelType.UP_HILL]
                        : heightDifference == 0
                        ? travelModifierMap[TravelType.EVEN]
                        : travelModifierMap[TravelType.DOWN_HILL]) as Double
                Double probability = (1 / (heightModifier * travelModifier)) * squareProbability
                nextSquares << [probability, SQUARE[0]]
            }
        }

        if (nextSquares.size() == 0) {
            SQUARE_PROBABILITIES.each { def SQUARE ->
                def (int sX, int sY) = SQUARE[0]
                def nX = x + sX
                def nY = y + sY
                def neighbor = nodeNetwork[nX][nY] as Node
                TravelType travelType = neighbor.travelType

                if (villager.canTravel(travelType)) {
                    nextSquares << [1d, SQUARE[0]]
                }
            }
        }

        def globalModifier = 100 / (nextSquares.sum { it[0] } as Double)
        nextSquares.each {
            it[0] *= globalModifier
        }

        Double sum = 0
        nextSquares.each { def square ->
            Double lowerLimit = sum
            Double upperLimit = lowerLimit + (square[0] as Double)
            sum = upperLimit
            square[0] = [lowerLimit, upperLimit]
        }

        if (Math.abs(nextSquares.last()[0][1] - 100) > 0.00000001) {
            throw new PerIsBorkenException()
        }

        return nextSquares
    }
}
