package main.npcLogic.stages.generic.rule


import main.calculator.Utility
import main.model.Villager
import main.npcLogic.Rule
import main.npcLogic.action.WalkAction

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
        def tileDest = Utility.generateTileXY()
        villager.actionQueue << new WalkAction(tileDest, walkActionClosure)
    }
}

