package main.model

import main.Main
import main.utility.Utility

class StraightPath {

    //start is excluded in the path, since we are already there
    Double[] start
    Queue<Double[]> path = new LinkedList<>()

    StraightPath(Double[] start, Double[] dest) {
        this.start = start
        Double[] nextStep = start
        while (!Utility.closeEnough(nextStep, dest)) {
            Double vx = dest[0] - nextStep[0]
            Double vy = dest[1] - nextStep[1]

            Double mag = Math.sqrt(vx * vx + vy * vy)

            vx /= mag
            vy /= mag
            Double px = (nextStep[0] + vx * Main.STEP)
            Double py = (nextStep[1] + vy * Main.STEP)
            nextStep = [px, py]
            path.add(nextStep)
        }
        path.add(dest)
    }
}
