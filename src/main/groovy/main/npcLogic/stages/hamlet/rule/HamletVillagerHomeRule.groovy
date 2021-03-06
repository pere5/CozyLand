package main.npcLogic.stages.hamlet.rule

import main.Main
import main.model.Villager
import main.npcLogic.Rule
import main.things.building.Hut
import main.utility.Utility

class HamletVillagerHomeRule extends Rule {

    HamletVillagerHomeRule(int rank) {
        this.rank = rank
    }

    @Override
    int status(Villager me) {
        if (!me.home && me.role.tribe.goodLocation) {
            BAD
        } else {
            GREAT
        }
    }

    @Override
    void planWork(Villager me, int status) {
        int[] tileXY = Utility.closeRandomTile(me, me.role.tribe.ruler.tileXY, Main.COMFORT_ZONE_TILES + 1, 1)
        me.home = new Hut(me, tileXY)
    }
}
