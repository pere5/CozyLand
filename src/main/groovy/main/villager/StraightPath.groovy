package main.villager

import main.Model
import main.model.Tile
import main.model.XYD
import main.model.XYI
import main.things.Artifact
import main.things.Drawable

import java.awt.*
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue

class StraightPath extends Action {
    Queue<XYD> path = new LinkedList<>()
    static Double STEP = 0.7
    int id

    StraightPath(XYD start, XYD dest, def nextTiles = null) {
        id = Model.getNewId()

        testPrints(start, dest, nextTiles)

        XYD nextStep = start
        while (!closeEnough(nextStep, dest)) {
            Double vx = dest[0] - nextStep[0]
            Double vy = dest[1] - nextStep[1]

            Double mag = Math.sqrt(vx * vx + vy * vy)

            vx /= mag
            vy /= mag
            Double px = (nextStep[0] + vx * STEP)
            Double py = (nextStep[1] + vy * STEP)
            nextStep = [px, py] as XYD
            path.add(nextStep)
        }
        path.add(dest)
    }

    private void testPrints(XYD start, XYD dest, def nextTiles) {
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

        def idx = Model.bresenham(pixelStart as XYI, pixelDest as XYI)
        (0..idx).each {
            def xy = Model.bufferedBresenhamResultArray[it] as XYI
            Model.model.drawables << new Artifact(parent: id, x: xy[0], y: xy[1])
        }

    }

    static boolean closeEnough(XYD pointA, XYD pointB) {
        Double xBig = pointA[0] + STEP
        Double xSmall = pointA[0] - STEP
        Double yBig = pointA[1] + STEP
        Double ySmall = pointA[1] - STEP
        return pointB[0] <= xBig && pointB[0] >= xSmall && pointB[1] <= yBig && pointB[1] >= ySmall
    }

    static boolean closeEnoughTile(XYI tileA, XYI tileB) {
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
