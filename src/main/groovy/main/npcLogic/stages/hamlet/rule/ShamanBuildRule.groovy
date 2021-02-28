package main.npcLogic.stages.hamlet.rule


import main.calculator.Utility
import main.model.Villager
import main.npcLogic.Rule
import main.npcLogic.action.ShapeAction
import main.npcLogic.action.WaitAction
import main.npcLogic.action.WalkAction
import main.things.Drawable.Shape

class ShamanBuildRule extends Rule {

    ShamanBuildRule(int rank) {
        this.rank = rank
    }

    @Override
    int status(Villager me) {
        if (me.role.tribe.goodLocation) {
            BAD
        } else {
            GOOD
        }
    }

    @Override
    void planWork(Villager me, int status) {
        if (Utility.compareTiles(me.tileXY, me.role.tribe.goodLocation.spot)) {
            me.actionQueue << new ShapeAction(Shape.SHAMAN_BUILD)
            me.actionQueue << new WaitAction(10)
        } else {
            me.actionQueue << new ShapeAction(Shape.SHAMAN)
            me.actionQueue << new WalkAction(me.role.tribe.goodLocation.spot)
        }
    }
}
