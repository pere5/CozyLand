package main.rule.shaman


import main.rule.Rule
import main.villager.Villager

class Migrate extends Rule {

    Migrate() {
        super(Integer.MAX_VALUE - 100)
    }

    @Override
    int status(Villager me) {
        GREAT
    }

    @Override
    void startWork(Villager villager, int status) {
        null
    }

    @Override
    void toNewState(Villager villager) {
        null
    }
}

