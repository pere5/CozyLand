package main.thread

import main.Model
import main.Node
import main.villager.StraightPath
import main.villager.Villager

class PathfinderWorker extends Worker {

    public static void main(String[] args) {
        def startIdx = [7, 5] as int[]
        def destIdx = [7, 2] as int[]

        def nodeIndices = new PathfinderWorker().bresenham(startIdx, destIdx)
        println(nodeIndices)
    }

    def update() {

        Model.model.villagers.grep { it.pathfinderWorker }.each { Villager villager ->

            def start = [villager.x, villager.y] as double[]
            def destination = Model.generateXY()

            def nodeNetwork = Model.model.nodeNetwork as Node[][]

            def startIdx = [0, 0] as int[]
            def destIdx = [1, 7] as int[]

            def nodeIndices = bresenham(startIdx, destIdx)

            //set all nodes oin this line to red and view the result

            villager.actionQueue << new StraightPath(start, destination)

            villager.toWorkWorker()
        }
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
