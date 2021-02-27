package main.npcLogic.stages.generic.rule

import main.Model
import main.npcLogic.action.WalkAction
import main.model.Villager
import main.npcLogic.Rule

class RandomBigWalkRule extends Rule {

    RandomBigWalkRule(int rank) {
        this.rank = rank
    }

    @Override
    int status(Villager me) {
        BAD
    }

    @Override
    void planWork(Villager villager, int status) {
        def tileDest = Model.generateTileXY()
        villager.actionQueue << new WalkAction(tileDest, new AffinityRule().&joinATribe)
    }
}

