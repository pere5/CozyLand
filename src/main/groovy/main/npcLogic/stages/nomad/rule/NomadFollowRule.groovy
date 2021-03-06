package main.npcLogic.stages.nomad.rule

import main.Main
import main.Model
import main.model.Villager
import main.npcLogic.Rule
import main.npcLogic.action.ShapeAction
import main.npcLogic.action.WalkAction
import main.utility.Utility

class NomadFollowRule extends Rule {

    NomadFollowRule(int rank) {
        this.rank = rank
    }

    @Override
    int status(Villager me) {
        if (Utility.withinCircle(me.tileXY, me.role.tribe.ruler.tileXY, Main.COMFORT_ZONE_TILES)) {
            GREAT
        } else {
            BAD
        }
    }

    @Override
    void planWork(Villager me, int status) {
        def tileDest = Utility.closeRandomTile(me, me.role.tribe.ruler.tileXY, Main.COMFORT_ZONE_TILES)
        me.actionQueue << new ShapeAction(Model.Shape.WARRIOR_STAGE_1)
        me.actionQueue << new WalkAction(tileDest)
    }
}

