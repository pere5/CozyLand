package main.rule

import main.model.Villager

class Gatherer extends Rule {
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
