package main.rule

import main.Model
import main.action.ShapeAction
import main.action.WaitAction
import main.action.WalkAction
import main.model.Villager
import main.things.Drawable.Shape

class ShamanBuildRule extends Rule {

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
        if (Model.compareTiles(me.tileXY, me.role.tribe.goodLocation.spot)) {
            me.actionQueue << new ShapeAction(Shape.SHAMAN_BUILD)
            me.actionQueue << new WaitAction(10)
        } else {
            me.actionQueue << new ShapeAction(Shape.SHAMAN)
            me.actionQueue << new WalkAction(me.role.tribe.goodLocation.spot)
        }
    }
}
