package main.villager

import main.Model
import main.Tile
import main.things.Artifact
import main.things.Drawable

import java.awt.*
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue

class StraightPath extends Action {
    Queue<Double[]> path = new LinkedList<>()
    static Double STEP = 0.7
    int id

    StraightPath(Double[] start, Double[] dest, def nextTiles = null) {
        id = Model.getNewId()

        testPrints(start, dest, nextTiles)

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

    private void testPrints(Double[] start, Double[] dest, def nextTiles) {
        def pixelStart = start
        def pixelDest = dest
        def (int x, int y) = Model.pixelToTileIdx(pixelStart)
        def tileNetwork = Model.model.tileNetwork as Tile[][]

        if (nextTiles) {
            def maxTile = nextTiles.max { def tile ->
                tile[0][1] - tile[0][0]
            }

            int maxProb = maxTile[0][1] - maxTile[0][0] + 1

            def colorGradient = Model.gradient(Color.DARK_GRAY, Color.WHITE, maxProb)

            nextTiles.each { def tile ->
                def (int nX, int nY) = tile[1]




                //hhhmmmzzz


                //def nX = x + sX
                //def nY = y + sY
                def tileProbability = (tile[0][1] - tile[0][0]) as Double
                def neighbor = tileNetwork[nX][nY] as Tile

                Model.model.drawables << new Artifact(
                        size: neighbor.size, parent: this.id, x: neighbor.x, y: neighbor.y,
                        color: colorGradient[tileProbability as int]
                )
            }
        }

        def idx = Model.bresenham(pixelStart as int[], pixelDest as int[])
        idx.times {
            def xy = Model.bufferedBresenhamResultArray[it]
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
        def resolution = path ? CONTINUE : DONE
        if (resolution == DONE) {
            (Model.model.drawables as ConcurrentLinkedQueue<Drawable>).removeAll { it.parent == id }
        }
        return resolution
    }
}
