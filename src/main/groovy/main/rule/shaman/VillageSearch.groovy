package main.rule.shaman

import main.Model
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
        shaman.tileQueue << Model.closeRandomTile(shaman, Villager.SHAMAN_DISTANCE_TILES)
        //shaman.actionQueue << 0
    }

    @Override
    void toNewState(Villager villager) {
        villager.toPathfinderWorker()
    }
}
