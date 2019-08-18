package main.rule

import main.Model
import main.villager.Villager

class RandomBigWalk extends Rule {

    @Override
    int status(Villager me) {
        BAD
    }

    @Override
    void startWork(Villager villager, int status) {
        def pixelDest = Model.generateTileXY()
        villager.tileQueue << pixelDest
    }

    @Override
    void toNewState(Villager villager) {
        villager.toPathfinderWorker()
    }
}

