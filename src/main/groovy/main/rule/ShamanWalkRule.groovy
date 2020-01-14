package main.rule

import main.Main
import main.Model
import main.action.SurveyAction
import main.action.WalkAction
import main.model.Villager

class ShamanWalkRule extends Rule {

    @Override
    int status(Villager villager) {
        BAD
    }

    @Override
    void planWork(Villager me, int status) {
        me.actionQueue << new WalkAction(Model.closeRandomTile(me, Main.SHAMAN_DISTANCE_TILES))
        me.actionQueue << new SurveyAction(10)
    }

    @Override
    void toNewState(Villager villager) {
        villager.toPathfinderWorker()
    }
}
