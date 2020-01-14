package main.rule

import main.Model
import main.action.WalkAction
import main.model.Villager

class RandomBigWalkRule extends Rule {

    @Override
    int status(Villager me) {
        BAD
    }

    @Override
    void planWork(Villager villager, int status) {
        def pixelDest = Model.generateTileXY()
        villager.actionQueue << new WalkAction(pixelDest)
    }

    @Override
    void toNewState(Villager villager) {
        villager.toPathfinderWorker()
    }
}

