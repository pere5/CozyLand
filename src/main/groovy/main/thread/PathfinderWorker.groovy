package main.thread

import main.Model
import main.Node
import main.villager.StraightPath
import main.villager.Villager

class PathfinderWorker extends Worker {

    def update() {

        Model.model.villagers.grep { it.planningPath }.each { Villager villager ->

            def start = [villager.x, villager.y] as double[]
            def destination = Model.generateXY()

            def nodeNetwork = Model.model.nodeNetwork as Node[][]

            def startNode = [0, 0] as int[]
            def destNode = [1, 7] as int[]

            //this seems bonkers
            def nodeIndices = bresenham(startNode, destNode)

            //set all nodes oin this line to red and view the result

            villager.actionQueue << new StraightPath(start, destination)

            villager.inWorking()
        }
    }

    List<int[]> bresenham(int x1, int y1, int x2, int y2) {
        def result = []
        int m_new = 2 * (y2 - y1)
        int slope_error_new = m_new - (x2 - x1)

        int x = x1, y = y1
        for (; x <= x2; x++) {
            //System.out.print("(" +x + "," + y + ")\n")
            result << [x, y] as int[]

            // Add slope to increment angle formed
            slope_error_new += m_new

            // Slope error reached limit, time to
            // increment y and update slope error.
            if (slope_error_new >= 0) {
                y++
                slope_error_new -= 2 * (x2 - x1)
            }
        }
        return result
    }
}
