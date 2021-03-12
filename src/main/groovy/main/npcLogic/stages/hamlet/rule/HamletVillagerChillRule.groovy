package main.npcLogic.stages.hamlet.rule

import main.Main
import main.model.Villager
import main.npcLogic.Rule
import main.npcLogic.action.ShapeAction
import main.npcLogic.action.WaitAction
import main.npcLogic.action.WalkAction
import main.things.Drawable
import main.utility.Utility

class HamletVillagerChillRule extends Rule {

    HamletVillagerChillRule(int rank) {
        this.rank = rank
    }

    @Override
    int status(Villager villager) {
        BAD
    }

    @Override
    void planWork(Villager me, int status) {

        def chillPlace = Utility.closeRandomTile(me, me.tileXY, Main.COMFORT_ZONE_TILES)
        def origShape = me.shape

        me.actionQueue << new ShapeAction(Drawable.Shape.CARAVEL)
        me.actionQueue << new WaitAction(3)
        me.actionQueue << new WalkAction(chillPlace)
        me.actionQueue << new WaitAction(3)
        me.actionQueue << new ShapeAction(origShape)
        me.actionQueue << new WaitAction(3)
    }
}
