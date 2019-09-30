package main.action

import main.Model
import main.TestPrints
import main.model.Villager

class StraightPath {
    Queue<Double[]> path = new LinkedList<>()

    Double[] a

    static Double STEP = 0.7

    StraightPath(Double[] start, Double[] dest, Villager villager) {

        //random place in tile here somewhere

        a = start

        TestPrints.testPrints(start, dest, villager)

        Double[] nextStep = start
        while (!Model.closeEnough(nextStep, dest)) {
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
}
