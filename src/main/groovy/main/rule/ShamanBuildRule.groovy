package main.rule

import main.action.ClosureAction
import main.action.ShapeAction
import main.action.WaitAction
import main.action.WalkAction
import main.model.Location
import main.model.Villager
import main.things.Drawable.Shape

class ShamanBuildRule extends Rule {

    @Override
    int status(Villager me) {
        if (me.role.tribe.goodLocation || me.metaObjects[ShamanNomadRule.toString()]) {
            BAD
        } else {
            GOOD
        }
    }

    @Override
    void planWork(Villager me, int status) {
        if (me.role.tribe.goodLocation) {
            me.actionQueue << new ShapeAction(Shape.SHAMAN_BUILD)
            me.actionQueue << new WaitAction(10)
        } else if (me.metaObjects[ShamanNomadRule.toString()]) {
            def location = me.metaObjects[ShamanNomadRule.toString()] as Location
            me.actionQueue << new ShapeAction(Shape.SHAMAN)
            me.actionQueue << new WalkAction(location.spot as int[])
            me.actionQueue << new ClosureAction({
                me.role.tribe.goodLocation = location
            })
        }
    }
}
