package main.rule

import main.Model
import main.villager.Villager
import main.villager.WalkPath

class Walk extends Rule {

    @Override
    int status(Villager villager) {
        return 0
    }

    @Override
    void startWork(Villager villager, int status) {
        villager.actionQueue << new WalkPath([villager.x, villager.y] as double[], Model.generateXY())
    }
}
