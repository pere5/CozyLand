package main.rule.shaman

import main.Model
import main.action.PathfinderAction
import main.action.Wait
import main.model.Villager
import main.rule.Rule

class VillageSearch extends Rule {

    @Override
    int status(Villager villager) {
        BAD
    }

    @Override
    void startWork(Villager shaman, int status) {
        assert shaman.role instanceof Shaman
        shaman.actionQueue << new PathfinderAction(Model.closeRandomTile(shaman, Villager.SHAMAN_DISTANCE_TILES))
        shaman.actionQueue << new Wait()
    }

    @Override
    void toNewState(Villager villager) {
        villager.toPathfinderWorker()
    }
}
