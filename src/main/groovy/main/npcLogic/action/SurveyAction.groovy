package main.npcLogic.action

import main.Main
import main.Model
import main.TestPrints
import main.model.Tile
import main.model.Villager
import main.npcLogic.Action
import main.npcLogic.Tribe
import main.npcLogic.stages.nomad.role.NomadShamanRole
import main.utility.Utility

class SurveyAction extends Action {

    int seconds
    long time
    Tribe tribe

    SurveyAction(int seconds, Tribe tribe) {
        this.tribe = tribe
        this.seconds = seconds
    }

    @Override
    boolean interrupt() {
        return false
    }

    @Override
    void switchWorker(Villager me) {
        throw new UnsupportedOperationException()
    }

    @Override
    boolean doIt(Villager shaman) {
        assert shaman.role instanceof NomadShamanRole

        if (!time) {
            time = System.currentTimeMillis() + (seconds * 1000)
        }

        List<Integer> shamanXY = shaman.getTileXY().collect { it as Integer }

        perInterval (2000) {
            def tileNetwork = Model.tileNetwork as Tile[][]
            shaman.role.tribe.villagers.each { Villager follower ->
                def (int tileX, int tileY) = follower.getTileXY()
                Utility.getTilesWithinRadii(tileX, tileY, Main.VISIBLE_ZONE_TILES) { int x, int y ->
                    TestPrints.printSurveyResourcesCircle(follower, x, y)
                    Tile tile = tileNetwork[x][y]
                    if (tile.naturalResources) {
                        if (tribe.surveyNaturalResources[shamanXY]) {
                            tribe.surveyNaturalResources[shamanXY].addAll(tile.naturalResources)
                        } else {
                            tribe.surveyNaturalResources[shamanXY] = tile.naturalResources.toSet()
                        }
                    }
                }
            }
        }

        def resolution = time > System.currentTimeMillis() ? CONTINUE : DONE

        if (resolution == DONE) {
            TestPrints.removeSurveyResourcesCircle(shaman.id)
        }

        return resolution
    }
}
