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
        /*def villageSpot = me.role.tribe.surveyResources.collectEntries {
            [it.key, it.value.unique { it.shape }.size()]
        }.max { it.value }*/
        GOOD
    }

    @Override
    void planWork(Villager me, int status) {
        me.actionQueue << new WalkAction(Model.closeRandomTile(me, Main.SHAMAN_DISTANCE_TILES_MIN, Main.SHAMAN_DISTANCE_TILES_MAX))
        me.actionQueue << new ShapeAction(Shape.SHAMAN_CAMP)
        me.actionQueue << new SurveyAction(6, me.role.tribe)
        me.actionQueue << new ShapeAction(Shape.SHAMAN)
    }

    @Override
    void toNewState(Villager villager) {
        villager.toPathfinderWorker()
    }
}
