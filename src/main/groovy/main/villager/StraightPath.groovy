package main.villager


import main.Model
import main.Node
import main.things.Artifact
import main.things.Drawable

import java.util.concurrent.ConcurrentLinkedQueue

class StraightPath extends Action {
    Queue<Double[]> path = new LinkedList<>()
    static Double STEP = 0.7
    int id

    StraightPath(Double[] start, Double[] dest, def nextSquares = null) {
        id = Model.getNewId()

        testPrints(start, dest, nextSquares)

        Double[] nextStep = start
        while (!closeEnough(nextStep, dest)) {
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

    private void testPrints(Double[] start, Double[] dest, def nextSquares) {
        def pixelStart = Model.round(start)
        def pixelDest = Model.round(dest)
        def (int x, int y) = Model.pixelToNodeIdx(pixelStart)
        def nodeNetwork = Model.model.nodeNetwork as Node[][]

/*        if (nextSquares) {
            def maxSquare = nextSquares.max { def square ->
                square[0][1] - square[0][0]
            }

            int maxProb = Model.round(maxSquare[0][1] - maxSquare[0][0] + 1)

            def colorGradient = Model.gradient(Color.DARK_GRAY, Color.WHITE, maxProb)

            nextSquares.each { def square ->
                def (int sX, int sY) = square[1]
                def nX = x + sX
                def nY = y + sY
                def squareProbability = (square[0][1] - square[0][0]) as Double
                def neighbor = nodeNetwork[nX][nY] as Node

                Model.model.drawables << new Artifact(
                        size: neighbor.size, parent: this.id, x: neighbor.x, y: neighbor.y,
                        color: colorGradient[Model.round(squareProbability)]
                )
            }
        }*/

        def idx = Model.bresenham(pixelStart, pixelDest)
        idx.times {
            def xy = Model.bufferedPathArray[it]
            Model.model.drawables << new Artifact(parent: id, x: xy[0], y: xy[1])
        }

    }

    static boolean closeEnough(Double[] pointA, Double[] pointB) {
        Double xBig = pointA[0] + STEP
        Double xSmall = pointA[0] - STEP
        Double yBig = pointA[1] + STEP
        Double ySmall = pointA[1] - STEP
        return pointB[0] <= xBig && pointB[0] >= xSmall && pointB[1] <= yBig && pointB[1] >= ySmall
    }

    static boolean closeEnoughNode(int[] nodeA, int[] nodeB) {
        int xBig = nodeA[0] + 1
        int xSmall = nodeA[0] - 1
        int yBig = nodeA[1] + 1
        int ySmall = nodeA[1] - 1
        return nodeB[0] <= xBig && nodeB[0] >= xSmall && nodeB[1] <= yBig && nodeB[1] >= ySmall
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
