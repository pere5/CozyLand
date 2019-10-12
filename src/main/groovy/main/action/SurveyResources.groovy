package main.action

import main.Model
import main.model.Tile
import main.model.Villager
import main.role.Shaman
import main.things.Drawable
import main.things.resource.Resource
import org.joda.time.LocalDateTime

class SurveyResources extends Action {

    int seconds
    Date time
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
            time = LocalDateTime.now().plusSeconds(seconds).toDate()
        }

        timesPerTenSeconds (3) {
            def tileNetwork = Model.tileNetwork as Tile[][]
            shaman.role.villagers.each { def follower ->
                def (int followerX, int followerY) = follower.getTileXY()
                Model.getPointsWithinRadii(followerX, followerY, Villager.VISIBLE_ZONE_TILES) { int x, int y ->
                    Tile tile = tileNetwork[x][y]
                    resources.addAll(tile.resources)
                }
            }
        }

        def resolution = time > new Date() ? CONTINUE : DONE

        if (resolution == DONE) {
            println(resources)
        }

        return resolution
    }
}
