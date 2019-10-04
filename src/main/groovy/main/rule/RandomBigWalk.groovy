package main.rule

import main.Model
import main.action.PathfinderAction
import main.model.Villager

class RandomBigWalk extends Rule {

    @Override
    int status(Villager me) {
        BAD
    }

    @Override
    void planWork(Villager villager, int status) {
        def pixelDest = Model.generateTileXY()
        villager.actionQueue << new PathfinderAction(pixelDest)
    }

    @Override
    void toNewState(Villager villager) {
        villager.toPathfinderWorker()
    }
}

