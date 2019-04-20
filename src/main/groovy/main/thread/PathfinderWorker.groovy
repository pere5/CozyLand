package main.thread

import main.Model
import main.exception.PerIsBorkenException
import main.villager.StraightPath
import main.villager.Villager

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

    static def DEGREE_PROBABILITY
    static def SQUARE_DEGREES

    private static void rewriteDegreesFile() {
        if (
                degreeRange(45) != (315..359) + (0..135) ||
                degreeRange(100) != 10..190 ||
                degreeRange(300) != (210..359) + (0..30)
        ) {
            throw new PerIsBorkenException()
        }

        def testDegrees = degreeRange(0)
        def testRange = probabilitiesForRange(testDegrees)

        if (testRange.keySet()*.toString() != (['sum'] + (testDegrees[0..180]))*.toString()) {
            throw new PerIsBorkenException()
        }

        if (Math.abs(testRange.sum - 100) > 0.00000001) {
            throw new PerIsBorkenException()
        }

        File file = new File("degreesFile.txt")
        file.write ''
        360.times {
            file << "${[it, probabilitiesForRange(degreeRange(it))]}\n"
        }
    }

    private static List<Integer> degreeRange (int degree) {
        int u = degree + 90
        int l = degree - 90
        int upper = u % 360
        int lower = l >= 0 ? l : l + 360
        (upper > lower) ? (lower..upper) : (lower..359) + (0..upper)
    }

    private static def probabilitiesForRange(List<Integer> degree) {
        def probabilityRange = degree.collectEntries {
            (
                    (degree[0..35]).collectEntries { [it, 12.5/36] } +
                    (degree[36..71]).collectEntries { [it, 25/36] } +
                    (degree[72..107]).collectEntries { [it, 25/36] } +
                    (degree[108..143]).collectEntries { [it, 25/36] } +
                    (degree[144..180]).collectEntries { [it, 12.5/37] }
            ).inject([sum:0.0]) { Map result, def entry ->
                def lowerLimit = result.sum
                def upperLimit = lowerLimit + entry.value
                result.sum = upperLimit
                entry.value = [lowerLimit, upperLimit]
                result << entry
            }
        }

        return probabilityRange
    }

    public static void main(String[] args) {
        if (false) {
            rewriteDegreesFile()
        }

        def startIdx = [7, 5] as int[]
        def destIdx = [7, 2] as int[]

        def nodeIndices = new PathfinderWorker().bresenham(startIdx, destIdx)

    }

    def update() {

        Model.model.villagers.grep { it.pathfinderWorker }.each { Villager villager ->

            def start = [villager.x, villager.y] as double[]
            def dest = Model.generateXY()
            /*
            def nodeNetwork = Model.model.nodeNetwork as Node[][]

            def startIdx = Model.round(start)
            def destIdx = Model.round(dest)

            def nodeStartIdx = nodeIdxToPixelIdx(startIdx)
            def nodeDestIdx = nodeIdxToPixelIdx(destIdx)

            def nodeIndices = bresenham(nodeStartIdx, nodeDestIdx)
            nodeIndices.each {
                nodeNetwork[it[0]][it[1]].color = Color.RED
            }*/

            villager.actionQueue << new StraightPath(start, dest)
            villager.toWorkWorker()
        }
    }

    int[] nodeIdxToPixelIdx(int[] ints) {

        //major stuff here


        ints.collect { it }
    }

    List<int[]> bresenham(int[] start, int[] dest) {
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
