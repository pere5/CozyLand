package main.npcLogic.stages.alone.rule

import main.model.Villager
import main.npcLogic.Rule
import main.npcLogic.action.WalkAction
import main.npcLogic.stages.alone.rule.AffinityRule
import main.utility.Utility

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
        def tileDest = Utility.generateTileXY()
        villager.actionQueue << new WalkAction(tileDest, AffinityRule.&joinATribe)
    }
}

