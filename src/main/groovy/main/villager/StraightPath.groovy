package main.villager

import main.things.Drawable

class StraightPath extends Action {
    Queue<double[]> path = new LinkedList<>()
    double STEP = 0.7

    StraightPath(double[] start, double[] destination) {

        double[] nextStep = start
        while (!closeEnough(nextStep, destination, STEP)) {
            double vx = destination[0] - nextStep[0]
            double vy = destination[1] - nextStep[1]

            double mag = Math.sqrt(vx * vx + vy * vy)

            vx /= mag
            vy /= mag
            double px = (nextStep[0] + vx * STEP)
            double py = (nextStep[1] + vy * STEP)
            nextStep = [px, py]
            path.add(nextStep)
        }
        path.add(destination)
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
        def (x, y) = path.poll()
        drawable.x = x
        drawable.y = y
        return path ? CONTINUE : DONE
    }
}
