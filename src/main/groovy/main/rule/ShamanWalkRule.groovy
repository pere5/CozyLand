package main.rule

import main.Main
import main.Model
import main.action.SurveyAction
import main.action.WaitAction
import main.action.WalkAction
import main.model.Villager

class ShamanWalkRule extends Rule {

    @Override
    int status(Villager villager) {
        BAD
    }

    @Override
    void planWork(Villager me, int status) {
        me.actionQueue << new WalkAction(Model.closeRandomTile(me, 5))
        me.actionQueue << new SurveyAction(2)
        me.actionQueue << new WalkAction(Model.closeRandomTile(me, 5))
        me.actionQueue << new SurveyAction(2)
        me.actionQueue << new WalkAction(Model.closeRandomTile(me, 5))
        me.actionQueue << new WaitAction(10)
    }

    @Override
    void toNewState(Villager villager) {
        villager.toPathfinderWorker()
    }
}
