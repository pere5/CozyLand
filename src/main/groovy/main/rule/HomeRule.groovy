package main.rule

import main.model.Villager

class HomeRule extends Rule {

    @Override
    int status(Villager me) {
        if (!me.home && me.role.tribe.goodLocation) {
            BAD
        } else {
            GREAT
        }
    }

    @Override
    void planWork(Villager villager, int status) {

    }
}
