package main.rule


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
            GREAT
        }
    }

    @Override
    void planWork(Villager me, int status) {
        me.actionQueue << new WalkAction(me.role.tribe.goodLocation.spot as int[])
        me.actionQueue << new ShapeAction(Shape.SHAMAN_BUILD)
        me.actionQueue << new WaitAction(10)
    }
}
