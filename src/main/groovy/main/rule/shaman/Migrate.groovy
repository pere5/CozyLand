package main.rule.shaman

import main.Model
import main.rule.Rule
import main.villager.Villager

class Migrate extends Rule {

    @Override
    int status(Villager me) {
        BAD
    }

    @Override
    void startWork(Villager me, int status) {
        me.tileQueue << Model.closeRandomTile(me.boss, Villager.COMFORT_ZONE_TILES)
    }

    @Override
    void toNewState(Villager villager) {
        villager.toPathfinderWorker()
    }
}

