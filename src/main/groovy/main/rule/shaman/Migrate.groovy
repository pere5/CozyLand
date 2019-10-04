package main.rule.shaman

import main.Model
import main.action.PathfinderAction
import main.model.Villager
import main.rule.Rule

class Migrate extends Rule {

    @Override
    int status(Villager me) {
        BAD
    }

    @Override
    void planWork(Villager me, int status) {
        me.actionQueue << new PathfinderAction(Model.closeRandomTile(me.boss, Villager.COMFORT_ZONE_TILES))
    }

    @Override
    void toNewState(Villager villager) {
        villager.toPathfinderWorker()
    }
}

