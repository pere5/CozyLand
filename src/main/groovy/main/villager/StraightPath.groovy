package main.villager

import main.Model
import main.Node
import main.things.Artifact
import main.things.Drawable
import main.thread.PathfinderWorker

import java.awt.*
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue

class StraightPath extends Action {
    Queue<Double[]> path = new LinkedList<>()
    Double STEP = 0.7
    int id

    StraightPath(Double[] start, Double[] dest) {
        id = Model.getNewId()
        def nodeIndices = PathfinderWorker.bresenham(Model.round(start), Model.round(dest))
        nodeIndices.each {
            Model.model.drawables << new Artifact(parent: id, x: it[0], y: it[1])
        }
/*
        def realDegree = calculateDegree(start, dest)
        def (int x, int y) = Model.pixelToNodeIdx(start)
        def nodeNetwork = Model.model.nodeNetwork as Node[][]
        def node = nodeNetwork[x][y]

        final def SQUARE_PROBABILITIES = Model.model.squareProbabilitiesForDegrees[realDegree]

        testPrints(SQUARE_PROBABILITIES, x, y, realDegree, nodeNetwork)
*/
        Double[] nextStep = start
        while (!closeEnough(nextStep, dest, STEP)) {
            Double vx = dest[0] - nextStep[0]
            Double vy = dest[1] - nextStep[1]

            Double mag = Math.sqrt(vx * vx + vy * vy)

            vx /= mag
            vy /= mag
            Double px = (nextStep[0] + vx * STEP)
            Double py = (nextStep[1] + vy * STEP)
            nextStep = [px, py]
            path.add(nextStep)
        }
        path.add(dest)
    }

    static def testGradient = Model.gradient(Color.BLACK, Color.WHITE, 32)
    private void testPrints(def SQUARE_PROBABILITIES, int x, int y, def realDegree, def nodeNetwork) {
        def node = nodeNetwork[x][y]
        SQUARE_PROBABILITIES.each { def SQUARE ->
            def (int sX, int sY) = SQUARE[0]
            def nX = x + sX
            def nY = y + sY
            def squareProbability = SQUARE[1] as Double
            def neighbor = nodeNetwork[nX][nY] as Node
            Model.model.drawables << new Artifact(
                    size: neighbor.size, parent: node.id, x: neighbor.x, y: neighbor.y,
                    color: testGradient[Model.round(squareProbability)]
            )
        }

    }

    boolean closeEnough(Double[] pointA, Double[] pointB, Double step) {
        Double xBig = pointA[0] + step
        Double xSmall = pointA[0] - step
        Double yBig = pointA[1] + step
        Double ySmall = pointA[1] - step
        return pointB[0] <= xBig && pointB[0] >= xSmall && pointB[1] <= yBig && pointB[1] >= ySmall
    }

    @Override
    boolean doIt(Drawable drawable) {
        def (Double x, Double y) = path.poll()
        drawable.x = x
        drawable.y = y
        def resolution = path ? CONTINUE : DONE
        if (resolution == DONE) {
            (Model.model.drawables as ConcurrentLinkedQueue<Drawable>).removeAll { it.parent == id }
        }
        return resolution
    }
}
