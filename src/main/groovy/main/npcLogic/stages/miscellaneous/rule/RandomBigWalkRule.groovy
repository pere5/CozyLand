package main.npcLogic.stages.miscellaneous.rule

import main.Model
import main.npcLogic.action.WalkAction
import main.model.Villager
import main.npcLogic.Rule
import main.npcLogic.stages.alone.rule.AffinityRule

class RandomBigWalkRule extends Rule {

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

