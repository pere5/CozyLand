package main

import javaSrc.color.ColorUtils
import main.model.Tile
import main.things.Artifact
import main.things.Drawable
import main.villager.StraightPath
import main.villager.Villager

import java.awt.*
import java.util.List
import java.util.concurrent.ConcurrentLinkedQueue

class TestPrints {

    static void testPrintsNextTiles(Double[] pixelStart, Double[] pixelDest, def nextTiles, Villager villager) {
        def (int x, int y) = Model.pixelToTileIdx(pixelStart)
        def tileNetwork = Model.tileNetwork as Tile[][]

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

                Model.drawables << new Artifact(
                        size: neighbor.size, parent: villager.id, x: neighbor.x, y: neighbor.y,
                        color: colorGradient[tileProbability as int]
                )
            }
        }
    }

    static void testPrints(Double[] pixelStart, Double[] pixelDest, Villager villager) {
        def idx = Model.bresenham(pixelStart as int[], pixelDest as int[])
        (0..idx).each {
            def xy = Model.bresenhamBuffer[it].clone()
            Model.drawables << new Artifact(size: 2, parent: villager.id, x: xy[0], y: xy[1], color: villager.testColor)
        }

    }

    static void testPrints(int[] tileStart, int[] tileDest, Villager villager, Set<List<Integer>> visited) {
        def pixelStart = Model.tileToPixelIdx(tileStart)
        def pixelDest = Model.tileToPixelIdx(tileDest)

        Random rand = new Random()
        villager.testColor = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat())

        visited.collect { Model.tileToPixelIdx(it) }.each {
            Model.drawables << new Artifact(
                    size: 3, parent: villager.id, x: it[0], y: it[1],
                    color: villager.testColor
            )
        }
        Model.drawables << new Artifact(
                size: 5, parent: villager.id, x: pixelDest[0], y: pixelDest[1],
                color: villager.testColor
        )
    }

    static void clearPrints(Villager villager) {
        (Model.drawables as ConcurrentLinkedQueue<Drawable>).removeAll { it.parent == villager.id }
    }

    static void printBresenhamMisses(Villager villager) {
        def count = 0
        for (int i = 0; i < villager.actionQueue.size() - 1; i++) {
            int[] a = Model.pixelToTileIdx((villager.actionQueue[i] as StraightPath).a)
            int[] b = Model.pixelToTileIdx((villager.actionQueue[i + 1] as StraightPath).a)
            if (Model.bresenhamBuffer[Model.bresenham(a, b, villager)].clone() != b) {
                count++
            }
        }
        if (count > 0) {
            println("${new ColorUtils().getColorNameFromRgb(villager.testColor.red, villager.testColor.green, villager.testColor.blue)}:${count}")
        }
    }

}
