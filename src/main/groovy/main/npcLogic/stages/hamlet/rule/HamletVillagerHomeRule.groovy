package main.npcLogic.stages.hamlet.rule


import main.model.Villager
import main.npcLogic.Rule

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

        i think we need an abstract home class?



        int[] tileXY = Utility.closeRandomTile(me, me.role.tribe.ruler.tileXY, Main.COMFORT_ZONE_TILES + 2, 1)
        me.actionQueue << new HomeAction(Hut.class)
    }
}
