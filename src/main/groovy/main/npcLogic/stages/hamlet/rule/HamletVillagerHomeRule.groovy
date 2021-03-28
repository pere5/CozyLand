package main.npcLogic.stages.hamlet.rule

import main.Main
import main.model.Villager
import main.npcLogic.Rule
import main.npcLogic.action.HomeAction
import main.npcLogic.action.ShapeAction
import main.npcLogic.action.WalkAction
import main.things.Drawable
import main.things.building.home.Hut
import main.utility.Utility

class HamletVillagerHomeRule extends Rule {

    HamletVillagerHomeRule(int rank) {
        this.rank = rank
    }

    @Override
    int status(Villager me) {
        if (me.home) {
            GREAT
        } else {
            BAD
        }
    }

    @Override
    void planWork(Villager me, int status) {
        int[] tileXY = Utility.closeRandomTile(me, me.role.tribe.ruler.tileXY, Main.COMFORT_ZONE_TILES + 1, 1)
        me.actionQueue << new ShapeAction(Drawable.Shape.HAMMER)
        me.actionQueue << new WalkAction(tileXY)
        me.actionQueue << new HomeAction(Hut.class)
    }
}
