package main.rule

import main.Model
import main.action.WalkAction
import main.exception.PerIsBorkenException
import main.model.Villager

class RandomBigWalkRule extends Rule {

    @Override
    int status(Villager me) {
        BAD
    }

    @Override
    void planWork(Villager villager, int status) {
        def tileDest = Model.generateTileXY()
        villager.actionQueue << new WalkAction(tileDest)
    }

    @Override
    void toNewState(Villager villager) {
        villager.toPathfinderWorker()
    }
}

