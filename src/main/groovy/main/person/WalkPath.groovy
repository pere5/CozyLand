package main.person

import main.things.Drawable

class WalkPath extends Action {
    Queue<double[]> path = new LinkedList<>()
    double STEP = 0.7

    WalkPath(double[] start, double[] destination) {

        //do stuff here to pathFind

        //https://www.redblobgames.com/pathfinding/a-star/implementation.html

        List<double[]> destinations = [
                destination
        ]

        double[] nextStep
        destinations.each { double[] dest ->
            while (!closeEnough(start, dest, STEP)) {
                double vx = dest[0] - start[0]
                double vy = dest[1] - start[1]

                double mag = Math.sqrt(vx * vx + vy * vy)

                vx /= mag
                vy /= mag
                double px = (start[0] + vx * STEP)
                double py = (start[1] + vy * STEP)
                nextStep = [px, py]
                path.add(nextStep)
                start = nextStep
            }
            path.add(dest)
        }
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
        return path
    }
}
