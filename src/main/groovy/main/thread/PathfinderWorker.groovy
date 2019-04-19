package main.thread

import main.Model
import main.Node
import main.exception.PerIsBorkenException
import main.villager.StraightPath
import main.villager.Villager

import java.awt.Color

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

    static def degreeProbabilities

    static {
        degreeProbabilities = (
                (0..35).collectEntries { [it, 12.5/36] } +
                (36..71).collectEntries { [it, 25/36] } +
                (72..107).collectEntries { [it, 25/36] } +
                (108..143).collectEntries { [it, 25/36] } +
                (144..180).collectEntries { [it, 12.5/37] }
        ).inject([sum:0.0]) { Map result, def entry ->
            def lowerLimit = result.sum
            def upperLimit = lowerLimit + entry.value
            result.sum = upperLimit
            entry.value = [lowerLimit: lowerLimit, upperLimit: upperLimit]
            result << entry
        }

        if (degreeProbabilities.keySet()*.toString() != (['sum'] + (0..180))*.toString()) {
            throw new PerIsBorkenException()
        }

        if (Math.abs(degreeProbabilities.sum - 100) > 0.00000001) {
            throw new PerIsBorkenException()
        }
    }

    public static void main(String[] args) {
        def startIdx = [7, 5] as int[]
        def destIdx = [7, 2] as int[]

        println()

        def nodeIndices = new PathfinderWorker().bresenham(startIdx, destIdx)
        println(nodeIndices)
    }

    def update() {

        Model.model.villagers.grep { it.pathfinderWorker }.each { Villager villager ->

            def start = [villager.x, villager.y] as double[]
            def dest = Model.generateXY()

            def nodeNetwork = Model.model.nodeNetwork as Node[][]

            def startIdx = Model.round(start)
            def destIdx = Model.round(dest)

            def nodeStartIdx = nodeIdxToPixelIdx(startIdx)
            def nodeDestIdx = nodeIdxToPixelIdx(destIdx)

            def nodeIndices = bresenham(nodeStartIdx, nodeDestIdx)
            nodeIndices.each {
                nodeNetwork[it[0]][it[1]].color = Color.RED
            }

            villager.actionQueue << new StraightPath(start, dest)
            villager.toWorkWorker()
        }
    }

    int[] nodeIdxToPixelIdx(int[] ints) {

        major stuff here


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
