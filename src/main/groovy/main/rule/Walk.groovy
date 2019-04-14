package main.rule

import main.Model
import main.villager.Villager
import main.villager.WalkPath

class Walk extends Rule {

    @Override
    int calculateStatus(Villager villager) {
        return 0
    }

    @Override
    void initWork(Villager villager, int status) {
        villager.actionQueue << new WalkPath([villager.x, villager.y] as double[], Model.generateXY())
    }
}
