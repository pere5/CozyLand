package main.thread


import main.Model
import main.Model.TravelType
import main.Node
import main.exception.PerIsBorkenException
import main.things.Artifact
import main.villager.StraightPath
import main.villager.Villager

import java.awt.*
import java.util.List

class PathfinderWorker extends Worker {

    /*
        - [ ] kör steg nod för nod
        - [ ] för nästa steg:
          - [ ] kör 90 grader med mittersta graden pekandes mot målet
          - [ ] fördela ut graderna lika till grann noderna
            - [ ] hårdkoda det med en färdig lösning per grad för alla 360 grader.
          - [ ] lägg upp en (normal) fördelning av sannolikhet för graderna över 90 grader.
            - [ ] hårdkoda fördelningen för 0 -> 90 grader med max i 45
          - [ ] beräkna genomsnittliga sannolikheten för varje grann nod relativt till de andra noderna utifrån gradernas sannolikheter
          - [ ] omfördela sannolikheterna mot vaje nod baserat på nodens movementCost relativt till de andra noderna
     */

    public static void main(String[] args) {

        Model.model = [
                squareProbabilitiesForDegrees: Model.calculateProbabilitiesModel()
        ]

        def start = [0, 0] as int[]
        def dest = [1, 1] as int[]

        def villager = new Villager()

        new PathfinderWorker().realSquareProbabilities(villager, start, dest)
        start = [0, 0] as int[]
        dest = [0, 1] as int[]

        new PathfinderWorker().realSquareProbabilities(villager, start, dest)
        start = [0, 0] as int[]
        dest = [-1, 0] as int[]

        new PathfinderWorker().realSquareProbabilities(villager, start, dest)
        start = [0, 0] as int[]
        dest = [0, -1] as int[]

        new PathfinderWorker().realSquareProbabilities(villager, start, dest)
    }

    def update() {

        for (Villager villager: Model.model.villagers) {
            if (villager.pathfinderWorker) {
                def start = [villager.x, villager.y] as Double[]
                def dest = Model.generateXY()

                def squares = realSquareProbabilities(villager, Model.round(start), Model.round(dest))

                println(squares)
                println(villager)

                villager.actionQueue << new StraightPath(start, dest)
                villager.toWorkWorker()
            }
        }
    }

    def realSquareProbabilities(Villager villager, int[] start, int[] dest) {

        def realSquareProbabilities = [:]

        def realDegree = calculateDegree(start, dest)
        def (int x, int y) = Model.pixelToNodeIdx(start)
        def nodeNetwork = Model.model.nodeNetwork as Node[][]
        def node = nodeNetwork[x][y]

        final def SQUARE_PROBABILITIES = Model.model.squareProbabilitiesForDegrees[realDegree]
        //testPrints(SQUARE_PROBABILITIES, nodeIdx, realDegree, nodeNetwork)

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

    static def testGradient = Model.gradient(Color.BLACK, Color.WHITE, 32)
    private void testPrints(def SQUARE_PROBABILITIES, def nodeIdx, def realDegree, def nodeNetwork) {
        def node = nodeNetwork[nodeIdx[0]][nodeIdx[1]]
        SQUARE_PROBABILITIES.each { def SQUARE ->

            Node neighbor = nodeNetwork[nodeIdx[0] + SQUARE[0][0]][nodeIdx[1] + SQUARE[0][1]]
            Model.model.drawables << new Artifact(
                    size: neighbor.size, parent: node.id, x: neighbor.x, y: neighbor.y,
                    color: testGradient[Model.round(SQUARE[1])]
            )
        }

    }

    static int calculateDegree(int[] start, int[] dest) {
        Double deg = Math.toDegrees(Math.atan2(dest[1] - start[1], dest[0] - start[0]))
        Model.round(deg >= 0 ? deg : deg + 360)
    }

    static List<int[]> bresenham(int[] start, int[] dest) {
        def (int x1, int y1) = start
        def (int x2, int y2) = dest
        def result = []

        // delta of exact value and rounded value of the dependent variable
        int d = 0

        int dx = Math.abs(x2 - x1)
        int dy = Math.abs(y2 - y1)

        int dx2 = 2 * dx // slope scaling factors to
        int dy2 = 2 * dy // avoid floating point

        int ix = x1 < x2 ? 1 : -1 // increment direction
        int iy = y1 < y2 ? 1 : -1

        int x = x1
        int y = y1

        if (dx >= dy) {
            while (true) {
                result << ([x, y] as int[])
                if (x == x2) {
                    break
                }
                x += ix
                d += dy2
                if (d > dx) {
                    y += iy
                    d -= dx2
                }
            }
        } else {
            while (true) {
                result << ([x, y] as int[])
                if (y == y2) {
                    break
                }
                y += iy
                d += dx2
                if (d > dy) {
                    x += ix
                    d -= dy2
                }
            }
        }

        return result
    }
}
