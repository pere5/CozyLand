package main.npcLogic.stages.nomad.rule

import main.Main
import main.model.Villager
import main.npcLogic.Rule
import main.npcLogic.action.ShapeAction
import main.npcLogic.action.WalkAction
import main.npcLogic.stages.nomad.NomadTribe
import main.things.Drawable.Shape
import main.utility.Utility

class NomadFollowRule extends Rule {

    NomadFollowRule(int rank) {
        this.rank = rank
    }

    @Override
    int status(Villager me) {

        int[] homeXY = getHomeXY(me)
        if (Utility.withinCircle(me.tileXY, homeXY, Main.COMFORT_ZONE_TILES)) {
            GREAT
        } else {
            BAD
        }
    }

    @Override
    void planWork(Villager me, int status) {
        int[] homeXY = getHomeXY(me)
        def tileDest = Utility.closeRandomTile(me, homeXY, Main.COMFORT_ZONE_TILES)
        me.actionQueue << new ShapeAction(Shape.FOLLOWER)
        me.actionQueue << new WalkAction(tileDest)
    }

    private static int[] getHomeXY(Villager me) {
        def tribe = me.role.tribe as NomadTribe
        me.home?.tileXY ?: tribe.ruler.tileXY
    }
}

