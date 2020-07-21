package main.rule

import main.Main
import main.Model
import main.action.ShapeAction
import main.action.SurveyAction
import main.action.WalkAction
import main.model.Villager
import main.things.Drawable.Shape

class ShamanWalkRule extends Rule {

    @Override
    int status(Villager me) {
        GOOD
    }

    @Override
    void planWork(Villager me, int status) {
        me.actionQueue << new WalkAction(Model.closeRandomTile(me, Main.SHAMAN_DISTANCE_TILES))
        me.actionQueue << new SurveyAction(4, me.role.tribe)
        me.actionQueue << new WalkAction(Model.closeRandomTile(me, Main.SHAMAN_DISTANCE_TILES))
        me.actionQueue << new SurveyAction(4, me.role.tribe)
        me.actionQueue << new WalkAction(Model.closeRandomTile(me, Main.SHAMAN_DISTANCE_TILES))
        me.actionQueue << new ShapeAction(Shape.SHAMAN_CAMP)
        me.actionQueue << new SurveyAction(10, me.role.tribe)
        me.actionQueue << new ShapeAction(Shape.SHAMAN)
    }

    @Override
    void toNewState(Villager villager) {
        villager.toPathfinderWorker()
    }
}
