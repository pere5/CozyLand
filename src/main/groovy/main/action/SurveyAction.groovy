package main.action

import main.Main
import main.Model
import main.TestPrints
import main.model.Tile
import main.model.Villager
import main.role.Tribe
import main.role.tribe.NomadTribe
import main.role.tribe.ShamanRole
import main.things.resource.Resource

class SurveyAction extends Action {

    int seconds
    long time
    Tribe tribe

    SurveyAction(int seconds, Tribe tribe) {
        this.tribe = tribe
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

        int[] shamanXY = shaman.getTileXY()

        perTenSeconds (6) {
            def tileNetwork = Model.tileNetwork as Tile[][]
            (shaman.role.tribe as NomadTribe).followers.each { Villager follower ->
                def (int tileX, int tileY) = follower.getTileXY()
                Model.getTilesWithinRadii(tileX, tileY, Main.VISIBLE_ZONE_TILES) { int x, int y ->
                    //TestPrints.printSurveyResourcesCircle(follower, x, y)
                    Tile tile = tileNetwork[x][y]
                    if (tile.resources) {
                        if (tribe.surveyResources[shamanXY]) {
                            tribe.surveyResources[shamanXY].addAll(tile.resources)
                        } else {
                            tribe.surveyResources[shamanXY] = tile.resources.toSet()
                        }
                    }
                }
            }
        }

        def resolution = time > System.currentTimeMillis() ? CONTINUE : DONE

        if (resolution == DONE) {
            //TestPrints.removeSurveyResourcesCircle(shaman.id)
            int i = 0
        }

        return resolution
    }
}
