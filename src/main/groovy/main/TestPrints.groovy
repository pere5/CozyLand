package main

import main.model.Tile
import main.things.Artifact
import main.things.Drawable
import main.villager.Villager

import java.awt.*
import java.util.List
import java.util.concurrent.ConcurrentLinkedQueue

class TestPrints {

    static void testPrints(Double[] start, Double[] dest, def nextTiles, Villager villager) {
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
        (0..idx).each {
            def xy = Model.bufferedBresenhamResultArray[it].clone()
            Model.model.drawables << new Artifact(parent: villager.id, x: xy[0], y: xy[1], color: villager.color)
        }

    }

    static void testPrints(Double[] pixelStart, Double[] pixelDest, Villager villager, Set<List<Integer>> visited) {
        Random rand = new Random()
        villager.color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat())

        visited.collect { Model.tileToPixelIdx(it) }.each {
            Model.model.drawables << new Artifact(
                    size: 3, parent: villager.id, x: it[0], y: it[1],
                    color: villager.color
            )
        }
        Model.model.drawables << new Artifact(
                size: 5, parent: villager.id, x: pixelDest[0], y: pixelDest[1],
                color: villager.color
        )
    }

    static void clearPrints(Villager villager) {
        (Model.model.drawables as ConcurrentLinkedQueue<Drawable>).removeAll { it.parent == villager.id }
    }
}
