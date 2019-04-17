package main.rule

import main.Model
import main.Node
import main.villager.StraightPath
import main.villager.Villager

class Walk extends Rule {

    @Override
    int status(Villager villager) {
        BAD
    }

    @Override
    void startWork(Villager villager, int status) {




        de här ska in i PathfinderWorker


        def start = [villager.x, villager.y] as double[]
        def destination = Model.generateXY()

        def nodeNetwork = Model.model.nodeNetwork as Node[][]

        def startNode = [0, 0] as int[]
        def destNode = [1, 7] as int[]

        //this seems bonkers
        def nodeIndices = nodeIndices(startNode, destNode)

        //set all nodes oin this line to red and view the result

        villager.actionQueue << new StraightPath(start, destination)
    }

    List<int[]> nodeIndices(int[] start, int[] dest) {

        def result = []

        def x0 = start[0] as int
        def y0 = start[1] as int
        def x1 = dest[0] as int
        def y1 = dest[1] as int

        def dx = x1 - x0
        def dy = y1 - y0
        def D = 2 * dy - dx
        def y = y0

        (x0..x1).each { def x ->
            result << [x, y]
            if (D > 0) {
                y = y + 1
                D = D - 2 * dx
            }
            D = D + 2 * dy
        }

        result
    }
}
