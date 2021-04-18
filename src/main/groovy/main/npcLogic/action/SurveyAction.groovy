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

    Tribe tribe

    SurveyAction(int waitSeconds, Tribe tribe) {
        this.tribe = tribe
        this.waitSeconds = waitSeconds
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
    Resolution doIt(Villager shaman) {
        assert shaman.role instanceof NomadShamanRole

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

        def resolution = waitForPeriod()

        if (resolution == Resolution.DONE) {
            TestPrints.removeSurveyResourcesCircle(shaman.id)
        }

        return resolution
    }
}
