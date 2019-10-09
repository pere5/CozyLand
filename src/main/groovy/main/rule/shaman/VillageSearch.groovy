package main.rule.shaman

import main.Model
import main.action.PathfinderAction
import main.action.SurveyResources
import main.model.Villager
import main.rule.Rule

class VillageSearch extends Rule {

    @Override
    int status(Villager villager) {
        BAD
    }

    @Override
    void planWork(Villager shaman, int status) {
        shaman.actionQueue << new PathfinderAction(Model.closeRandomTile(shaman, Villager.SHAMAN_DISTANCE_TILES))
        shaman.actionQueue << new SurveyResources(10)
    }

    @Override
    void toNewState(Villager villager) {
        villager.toPathfinderWorker()
    }
}
