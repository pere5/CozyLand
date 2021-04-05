package main.npcLogic.stages.hamlet.rule

import main.Model
import main.model.Villager
import main.npcLogic.Rule
import main.npcLogic.action.ShapeAction
import main.npcLogic.action.WaitAction
import main.npcLogic.action.WalkAction
import main.utility.Utility

class HamletChieftainRule extends Rule {

    HamletChieftainRule(int rank) {
        this.rank = rank
    }

    @Override
    int status(Villager me) {
        if (me.role.tribe.location) {
            BAD
        } else {
            GOOD
        }
    }

    @Override
    void planWork(Villager me, int status) {
        if (Utility.compareTiles(me.tileXY, me.role.tribe.location.spot)) {
            me.actionQueue << new ShapeAction(Model.Shape.BUILDER)
            me.actionQueue << new WaitAction(10)

        } else {
            me.actionQueue << new ShapeAction(Model.Shape.SHAMAN)
            me.actionQueue << new WalkAction(me.role.tribe.location.spot)
        }
    }
}
