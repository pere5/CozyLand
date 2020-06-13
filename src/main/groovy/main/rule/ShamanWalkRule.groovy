package main.rule


import main.Model
import main.action.ShapeAction
import main.action.SurveyAction
import main.action.WaitAction
import main.action.WalkAction
import main.model.Villager
import main.things.Drawable.SHAPE

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
        me.actionQueue << new ShapeAction(SHAPE.SHAMAN_CAMP)
        me.actionQueue << new WaitAction(10)
        me.actionQueue << new ShapeAction(SHAPE.SHAMAN)
    }

    @Override
    void toNewState(Villager villager) {
        villager.toPathfinderWorker()
    }
}
