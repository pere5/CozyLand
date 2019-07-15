package main.rule

import main.Model
import main.villager.Villager

class Walk extends Rule {

    @Override
    int status(Villager villager) {
        BAD
    }

    @Override
    void startWork(Villager villager, int status) {
        def pixelDest = Model.generateTileXY()
        villager.tileQueue << pixelDest
    }

    @Override
    void stateWhenDone(Villager villager) {
        villager.toPathfinderWorker()
    }
}
