package main.model

import main.Main
import main.Model
import main.TestPrints
import main.model.Villager

class StraightPath {
    Queue<Double[]> path = new LinkedList<>()

    Double[] a

    StraightPath(Double[] start, Double[] dest, Villager villager) {

        //random place in tile here somewhere

        a = start

        TestPrints.straightPathTestPrints(start, dest, villager)

        Double[] nextStep = start
        while (!Model.closeEnough(nextStep, dest)) {
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
