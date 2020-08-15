package main.rule

import main.Main
import main.Model
import main.action.ShapeAction
import main.action.WalkAction
import main.model.Villager
import main.role.tribe.NomadTribe
import main.things.Drawable.Shape

class FollowRule extends Rule {

    @Override
    int status(Villager me) {

        int[] homeXY = getHomeXY(me)
        if (Model.withinCircle(me.tileXY, homeXY, Main.COMFORT_ZONE_TILES)) {
            GREAT
        } else {
            BAD
        }
    }

    @Override
    void planWork(Villager me, int status) {
        int[] homeXY = getHomeXY(me)
        def tileDest = Model.closeRandomTile(me, homeXY, Main.COMFORT_ZONE_TILES)
        me.actionQueue << new ShapeAction(Shape.FOLLOWER)
        me.actionQueue << new WalkAction(tileDest)
    }

    private static int[] getHomeXY(Villager me) {
        def tribe = me.role.tribe as NomadTribe
        me.home?.tileXY ?: tribe.shaman.tileXY
    }
}

