package main.npcLogic.stages.hamlet.rule

import main.model.Villager
import main.npcLogic.Rule

class GathererRule extends Rule {

    GathererRule(int rank) {
        this.rank = rank
    }

    @Override
    int status(Villager villager) {
        def enoughResourcesToBuild = false
        if (enoughResourcesToBuild) {
            GOOD
        } else {
            BAD
        }
    }

    @Override
    void planWork(Villager villager, int status) {

    }
}
