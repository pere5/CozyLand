package main.rule

import main.Main
import main.Model
import main.action.ShapeAction
import main.action.WalkAction
import main.model.Villager
import main.role.tribe.NomadTribe
import main.things.Drawable
import main.things.resource.Stone
import main.things.resource.Wood

class BuilderRule extends Rule {

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

        def tribe = me.role.tribe as NomadTribe
        def resources = tribe.resources

        if (Model.withinCircle(me.tileXY, tribe.shaman.tileXY, Main.NEXT_TO_TILES)) {

            me.actionQueue << new ShapeAction(Drawable.Shape.FOLLOWER_BUILDER)

            def wood = resources.count { it instanceof Wood }
            def stone = resources.count { it instanceof Stone }

            if (wood >= 2 && stone >= 1) {

            } else {

            }
        } else {
            def tileDest = Model.closeRandomTile(tribe.shaman, null, Main.NEXT_TO_TILES)
            me.actionQueue << new WalkAction(tileDest)
        }
    }
}
