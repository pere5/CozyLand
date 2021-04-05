package main.npcLogic.stages.hamlet.rule

import main.Main
import main.Model
import main.model.Villager
import main.npcLogic.Rule
import main.npcLogic.action.ShapeAction
import main.npcLogic.action.WaitAction
import main.npcLogic.action.WalkAction
import main.things.resource.Stone
import main.things.resource.Wood
import main.utility.Utility

class HamletVillagerBuilderRule extends Rule {

    HamletVillagerBuilderRule(int rank) {
        this.rank = rank
    }

    @Override
    int status(Villager me) {
        def unfinishedBuildings = [true]
        //if (unfinishedBuildings) {
        if (me.role.tribe.location) {
            BAD
        } else {
            GREAT
        }
    }

    @Override
    void planWork(Villager me, int status) {
        def unfinishedBuildings = []
        def unfinishedBuilding = unfinishedBuildings[
                Utility.getRandomIntegerBetween(0, unfinishedBuildings.size() - 1)
        ]

        def tribe = me.role.tribe
        def resources = tribe.resources

        me.actionQueue << new ShapeAction(Model.Shape.VILLAGER)
        me.actionQueue << new WaitAction(2)

        def tileDest = Utility.closeRandomTile(me, tribe.ruler.tileXY, Main.COMFORT_ZONE_TILES)
        me.actionQueue << new ShapeAction(Model.Shape.BUILDER)
        me.actionQueue << new WalkAction(tileDest)

        me.actionQueue << new ShapeAction(Model.Shape.VILLAGER)
        me.actionQueue << new WaitAction(10)
        def wood = resources.findAll { it instanceof Wood }
        def stone = resources.findAll { it instanceof Stone }

        def neededWood = Model.buildingResources[Model.Shape.HUT][Model.Shape.WOOD]
        def neededStone = Model.buildingResources[Model.Shape.HUT][Model.Shape.STONE]

        def enoughWood = wood.size() >= neededWood
        def enoughStone = stone.size() >= neededStone

        //hur ska vi bygga detta enkelt?

        if (enoughWood && enoughStone) {

        } else {

        }
    }
}
