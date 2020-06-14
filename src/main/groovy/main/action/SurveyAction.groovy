package main.action

import main.Main
import main.Model
import main.model.Tile
import main.model.Villager
import main.role.ShamanRole
import main.things.resource.Resource

class SurveyAction extends Action {

    int seconds
    long time
    Set<Resource> resources = []

    SurveyAction(int seconds) {
        this.seconds = seconds
    }

    @Override
    void switchWorker(Villager me) {
        throw new UnsupportedOperationException()
    }

    @Override
    boolean doIt(Villager shaman) {
        assert shaman.role instanceof ShamanRole

        if (!time) {
            time = System.currentTimeMillis() + (seconds * 1000)
        }

        perTenSeconds (3) {
            def tileNetwork = Model.tileNetwork as Tile[][]
            shaman.role.followers.each { def follower ->
                def (int tileX, int tileY) = follower.getTileXY()
                Model.getTilesWithinRadii(tileX, tileY, Main.VISIBLE_ZONE_TILES) { int x, int y ->
                    //TestPrints.printSurveyResourcesCircle(me, x, y)
                    Tile tile = tileNetwork[x][y]
                    resources.addAll(tile.resources)
                }
            }
        }

        def resolution = time > System.currentTimeMillis() ? CONTINUE : DONE

        if (resolution == DONE) {
            Model.drawables.removeAll { it.parent == shaman.id }
        }

        return resolution
    }
}
