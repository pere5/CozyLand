package main.rule

import main.Model
import main.villager.StraightPath
import main.villager.Villager

class Walk extends Rule {

    @Override
    int status(Villager villager) {
        return 0
    }

    @Override
    void startWork(Villager villager, int status) {
        villager.actionQueue << new StraightPath([villager.x, villager.y] as double[], Model.generateXY())
    }
}
