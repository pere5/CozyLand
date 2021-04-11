package main.npcLogic.stages.hamlet.rule


import main.Model
import main.model.Villager
import main.npcLogic.Rule
import main.npcLogic.action.HomeAction
import main.npcLogic.action.ShapeAction
import main.npcLogic.action.WalkAction
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

        def noBuildings = me.role.tribe.buildings.size()
        def spot = me.role.tribe.location.spot

        //använd gyllene snittet / golden ratio och rotera runt
        int[] tileXY = buildingPositionByGoldenRatio(me, spot, noBuildings)
        int i = 0
        while (!Utility.withinTileNetwork(tileXY[0], tileXY[1])) {
            tileXY = buildingPositionByGoldenRatio(me, spot, noBuildings + (++i))
        }
        me.actionQueue << new ShapeAction(Model.Shape.BUILDER)
        me.actionQueue << new WalkAction(tileXY)
        me.actionQueue << new HomeAction(Hut.class)
    }

    private int[] buildingPositionByGoldenRatio(Villager me, int[] spotXY, int multiplier) {
        def (int spotX, int spotY) = spotXY
        def angle = multiplier * (2 * Math.PI) / Model.GOLDEN_RATIO
        def distance = 50 * me.role.tribe.buildings.size()
        def pointX = (spotX + distance * Math.cos(angle)) as Integer
        def pointY = (spotY + distance * Math.sin(angle)) as Integer
        def tileXY = [pointX, pointY] as int[]
        tileXY
    }
}
