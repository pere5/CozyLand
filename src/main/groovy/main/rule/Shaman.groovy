package main.rule

import main.Main
import main.Model
import main.action.PathfinderAction
import main.action.SurveyResources
import main.model.Villager
import main.rule.Rule

class Shaman extends Rule {

    @Override
    int status(Villager villager) {
        BAD
    }

    @Override
    void planWork(Villager me, int status) {
        me.actionQueue << new PathfinderAction(Model.closeRandomTile(me, Main.SHAMAN_DISTANCE_TILES))
        me.actionQueue << new SurveyResources(10)
    }

    @Override
    void toNewState(Villager villager) {
        villager.toPathfinderWorker()
    }
}
