package main

import javaSrc.color.ColorUtils
import main.model.Path
import main.model.Tile
import main.model.Villager
import main.npcLogic.action.WalkAction
import main.things.Artifact
import main.things.ArtifactLine
import main.things.Drawable
import main.things.Drawable.Shape
import main.utility.ImageUtils
import main.utility.Utility

import java.awt.*
import java.util.List

class TestPrints {

    static final boolean DEBUG_PATH_PRINTS = false
    static final boolean ONLY_RULERS = true

    static final boolean DEBUG_DOTTED_PRINTS = false

    static final boolean PRINT_BRESENHAM_MISSES = false

    static void testPrintsNextTiles(Double[] pixelStart, Double[] pixelDest, def nextTiles, Villager villager) {
        def (int x, int y) = Utility.pixelToTileIdx(pixelStart)
        def tileNetwork = Model.tileNetwork as Tile[][]

        if (nextTiles) {
            def maxTile = nextTiles.max { def tile ->
                tile[0][1] - tile[0][0]
            }

            int maxProb = maxTile[0][1] - maxTile[0][0] + 1

            def colorGradient = ImageUtils.gradient(Color.DARK_GRAY, Color.WHITE, maxProb)

            nextTiles.each { def tile ->
                def (int nX, int nY) = tile[1]

                //hhhmmmzzz

                //def nX = x + sX
                //def nY = y + sY
                def tileProbability = (tile[0][1] - tile[0][0]) as Double
                def neighbor = tileNetwork[nX][nY] as Tile

                new Artifact(
                        size: neighbor.size, parent: villager.id, x: neighbor.x, y: neighbor.y,
                        color: colorGradient[tileProbability as int]
                )
            }
        }
    }

    static void straightPathTestPrints(Double[] pixelStart, Double[] pixelDest, Villager villager) {
        if (!DEBUG_PATH_PRINTS) return
        if (ONLY_RULERS ? (villager.id != villager.role.tribe.ruler.id) : false) return

        new ArtifactLine(size: 1, parent: villager.id, orig: pixelStart, dest: pixelDest, color: villager.testColor, shape: Shape.LINE)
    }

    static void perStarTestPrints(int[] tileStart, int[] tileDest, Villager villager, Set<List<Integer>> visited) {
        if (!DEBUG_DOTTED_PRINTS) return

        def pixelStart = Utility.tileToPixelIdx(tileStart)
        def pixelDest = Utility.tileToPixelIdx(tileDest)

        visited.collect { Utility.tileToPixelIdx(it) }.each {
            new Artifact(
                    size: 3, parent: villager.id, x: it[0], y: it[1],
                    color: villager.testColor
            )
        }
        new Artifact(
                size: 5, parent: villager.id, x: pixelDest[0], y: pixelDest[1],
                color: villager.testColor
        )
    }

    static void clearPrints(Villager villager) {
        if (!DEBUG_PATH_PRINTS) return

        Model.drawables.removeAll { it.parent == villager.id }
    }

    static void printBresenhamMisses(Villager villager) {
        if (!PRINT_BRESENHAM_MISSES) return

        def count = 0
        villager.actionQueue.findAll { it instanceof WalkAction }.collect { it as WalkAction }.each { WalkAction walkAction ->
            for (int i = 0; i < walkAction.pathQueue.size() - 1; i++) {
                int[] a = Utility.pixelToTileIdx(walkAction.pathQueue[i].start)
                int[] b = Utility.pixelToTileIdx(walkAction.pathQueue[i + 1].start)
                if (Path.bresenhamBuffer[Path.bresenham(a, b, villager)].clone() != b) {
                    println("${a} - ${b}")
                    count++
                }
            }
        }
        if (count > 0) {
            println("${new ColorUtils().getColorNameFromRgb(villager.testColor.red, villager.testColor.green, villager.testColor.blue)}:${count}")
        }
    }

    static def printRadii(int x, int y, Villager me) {
        if (!DEBUG_DOTTED_PRINTS) return

        new Artifact(
                size: 3, parent: me.id, x: Utility.tileToPixelIdx(x), y: Utility.tileToPixelIdx(y),
                color: me.testColor
        )
    }

    static void printSurveyResourcesCircle(Drawable me, int x, int y) {
        if (!DEBUG_DOTTED_PRINTS) return

        new Artifact(size: 2, parent: me.id, x: Utility.tileToPixelIdx(x), y: Utility.tileToPixelIdx(y), color: Color.BLUE)
    }

    static void removeSurveyResourcesCircle(int id) {
        if (!DEBUG_DOTTED_PRINTS) return

        Model.drawables.removeAll { it.parent == id }
    }
}
