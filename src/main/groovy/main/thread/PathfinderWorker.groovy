package main.thread

import main.Main
import main.Model
import main.Node
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

        new PathfinderWorker().realSquareProbabilities(start, dest)
        start = [0, 0] as int[]
        dest = [0, 1] as int[]

        new PathfinderWorker().realSquareProbabilities(start, dest)
        start = [0, 0] as int[]
        dest = [-1, 0] as int[]

        new PathfinderWorker().realSquareProbabilities(start, dest)
        start = [0, 0] as int[]
        dest = [0, -1] as int[]

        new PathfinderWorker().realSquareProbabilities(start, dest)
    }

    def update() {

        for (Villager villager: Model.model.villagers) {
            if (villager.pathfinderWorker) {
                def start = [villager.x, villager.y] as Double[]
                def dest = Model.generateXY()

                def square = realSquareProbabilities(Model.round(start), Model.round(dest))


                villager.actionQueue << new StraightPath(start, dest)
                villager.toWorkWorker()
            }
        }
    }

    static def testGradient = Model.gradient(Color.BLACK, Color.WHITE, 32)

    def realSquareProbabilities(int[] start, int[] dest) {

        def realDegree = calculateDegree(start, dest)
        def nodeIdx = pixelToNodeIdx(start)
        def nodeNetwork = Model.model.nodeNetwork as Node[][]
        def node = nodeNetwork[nodeIdx[0]][nodeIdx[1]]

        final def SQUARE_PROBABILITIES = Model.model.squareProbabilitiesForDegrees[realDegree]


        SQUARE_PROBABILITIES.each { def SQUARE ->

            Node neighbor = nodeNetwork[nodeIdx[0] + SQUARE[0][0]][nodeIdx[1] + SQUARE[0][1]]
            testPrints(SQUARE, node, neighbor, realDegree)
        }

        int boll = 0
        null
    }

    private void testPrints(def SQUARE, def node, def neighbor, def realDegree) {
        println("${Model.round(SQUARE[1])}\t${SQUARE[0][0]}\t${SQUARE[0][1]}\t${realDegree}")
        Model.model.drawables << new Artifact(
                size: neighbor.size, parent: node.id, x: neighbor.x, y: neighbor.y,
                color: testGradient[Model.round(SQUARE[1])]
        )
    }

    static int[] pixelToNodeIdx(int[] ints) {
        ints.collect { it / Main.SQUARE_WIDTH }
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
