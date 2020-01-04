package main.action

import main.Main
import main.Model
import main.model.Tile
import main.model.Villager
import main.role.Shaman
import main.things.Artifact
import main.things.Drawable
import main.things.resource.Resource

import java.awt.*

class SurveyResources extends Action {

    int seconds
    long time
    Set<Resource> resources = []

    SurveyResources (int seconds) {
        this.seconds = seconds
    }

    @Override
    boolean doIt(Drawable me) {
        assert me instanceof Villager
        def shaman = me as Villager
        assert shaman.role instanceof Shaman

        if (!time) {
            time = System.currentTimeMillis() + (seconds * 1000)
        }

        perTenSeconds (3) {
            def tileNetwork = Model.tileNetwork as Tile[][]
            shaman.role.villagers.each { def follower ->
                def (int tileX, int tileY) = follower.getTileXY()
                Model.getPointsWithinRadii(tileX, tileY, Main.VISIBLE_ZONE_TILES) { int x, int y ->
                    Model.drawables << new Artifact(size: 2, parent: me.id, x: Model.tileToPixelIdx(x), y: Model.tileToPixelIdx(y), color: Color.BLUE)
                    if (x >= 0 && x <= tileNetwork.length - 1 && y >= 0 && y <= tileNetwork[0].length - 1) {
                        Tile tile = tileNetwork[x][y]
                        resources.addAll(tile.resources)
                    }

                }
            }
        }

        def resolution = time > System.currentTimeMillis() ? CONTINUE : DONE

        if (resolution == DONE) {
            Model.drawables.removeAll { it.parent == me.id }
        }

        return resolution
    }
}
