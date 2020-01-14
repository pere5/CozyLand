package main.action

import main.Main
import main.Model
import main.model.Tile
import main.model.Villager
import main.role.Shaman
import main.things.Drawable
import main.things.resource.Resource

class SurveyAction extends Action {

    int seconds
    long time
    Set<Resource> resources = []

    SurveyAction(int seconds) {
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
                Model.getTilesWithinRadii(tileX, tileY, Main.VISIBLE_ZONE_TILES) { int x, int y ->
                    //TestPrints.printSurveyResourcesCircle(me, x, y)
                    Tile tile = tileNetwork[x][y]
                    resources.addAll(tile.resources)
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