package main.villager

import main.Model
import main.things.Artifact
import main.things.Drawable
import main.thread.PathfinderWorker

class StraightPath extends Action {
    Queue<double[]> path = new LinkedList<>()
    double STEP = 0.7
    int id

    StraightPath(double[] start, double[] dest) {
        id = Model.getNewId()
        def nodeIndices = PathfinderWorker.bresenham(Model.round(start), Model.round(dest))
        nodeIndices.each {
            Model.model.drawables << new Artifact(parent: id, x: it[0], y: it[1])
        }

        double[] nextStep = start
        while (!closeEnough(nextStep, dest, STEP)) {
            double vx = dest[0] - nextStep[0]
            double vy = dest[1] - nextStep[1]

            double mag = Math.sqrt(vx * vx + vy * vy)

            vx /= mag
            vy /= mag
            double px = (nextStep[0] + vx * STEP)
            double py = (nextStep[1] + vy * STEP)
            nextStep = [px, py]
            path.add(nextStep)
        }
        path.add(dest)
    }

    boolean closeEnough(double[] pointA, double[] pointB, double step) {
        double xBig = pointA[0] + step
        double xSmall = pointA[0] - step
        double yBig = pointA[1] + step
        double ySmall = pointA[1] - step
        return pointB[0] <= xBig && pointB[0] >= xSmall && pointB[1] <= yBig && pointB[1] >= ySmall
    }

    @Override
    boolean doIt(Drawable drawable) {
        def (double x, double y) = path.poll()
        drawable.x = x
        drawable.y = y
        def resolution = path ? CONTINUE : DONE
        if (resolution == DONE) {
            (Model.model.drawables as List).removeAll { it.parent == id }
        }
        return resolution
    }
}
