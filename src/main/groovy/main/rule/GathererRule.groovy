package main.rule

import main.model.Villager

class GathererRule extends Rule {
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
