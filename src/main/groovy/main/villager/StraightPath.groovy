package main.villager

import main.TestPrints
import main.things.Drawable

class StraightPath extends Action {
    Queue<Double[]> path = new LinkedList<>()

    Double[] a

    static Double STEP = 0.7

    StraightPath(Double[] start, Double[] dest, Villager villager) {
        a = start

        TestPrints.testPrints(start, dest, villager)

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

    static boolean closeEnough(Double[] pointA, Double[] pointB) {
        Double xBig = pointA[0] + STEP
        Double xSmall = pointA[0] - STEP
        Double yBig = pointA[1] + STEP
        Double ySmall = pointA[1] - STEP
        return pointB[0] <= xBig && pointB[0] >= xSmall && pointB[1] <= yBig && pointB[1] >= ySmall
    }

    static boolean closeEnoughTile(int[] tileA, int[] tileB) {
        int xBig = tileA[0] + 1
        int xSmall = tileA[0] - 1
        int yBig = tileA[1] + 1
        int ySmall = tileA[1] - 1
        return tileB[0] <= xBig && tileB[0] >= xSmall && tileB[1] <= yBig && tileB[1] >= ySmall
    }

    @Override
    boolean doIt(Drawable drawable) {
        def (Double x, Double y) = path.poll()
        drawable.x = x
        drawable.y = y
        return  path ? CONTINUE : DONE
    }
}
