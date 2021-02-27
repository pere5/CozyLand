package main.npcLogic.stages.generic.rule

import main.Model
import main.npcLogic.action.WalkAction
import main.model.Villager
import main.npcLogic.Rule

class RandomBigWalkRule extends Rule {

    Closure walkActionClosure

    RandomBigWalkRule(int rank) {
        this.rank = rank
    }

    RandomBigWalkRule(int rank, Closure walkActionClosure) {
        this.rank = rank
        this.walkActionClosure = walkActionClosure
    }

    @Override
    int status(Villager me) {
        BAD
    }

    @Override
    void planWork(Villager villager, int status) {
        def tileDest = Model.generateTileXY()
        villager.actionQueue << new WalkAction(tileDest, walkActionClosure)
    }
}

