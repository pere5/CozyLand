package main.rule.shaman

import main.Model
import main.rule.Rule
import main.villager.Villager

class VillageSearch extends Rule {

    @Override
    int status(Villager villager) {
        BAD
    }

    @Override
    void startWork(Villager shaman, int status) {
        assert shaman.role instanceof Shaman
        shaman.tileQueue << Model.closeRandomTile(shaman, Villager.SHAMAN_DISTANCE_TILES)
    }

    @Override
    void toNewState(Villager villager) {
        villager.toPathfinderWorker()
    }
}
