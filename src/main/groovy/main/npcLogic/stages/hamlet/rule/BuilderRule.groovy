package main.npcLogic.stages.hamlet.rule


import main.Model
import main.npcLogic.action.ShapeAction
import main.npcLogic.action.WaitAction
import main.model.Villager
import main.npcLogic.stages.nomad.NomadTribe
import main.npcLogic.Rule
import main.things.Drawable.Shape
import main.things.resource.Stone
import main.things.resource.Wood

class BuilderRule extends Rule {

    BuilderRule(int rank) {
        this.rank = rank
    }

    @Override
    int status(Villager me) {
        def unfinishedBuildings = [true]
        //if (unfinishedBuildings) {
        if (me.role.tribe.goodLocation) {
            BAD
        } else {
            GREAT
        }
    }

    @Override
    void planWork(Villager me, int status) {
        def unfinishedBuildings = []
        def unfinishedBuilding = unfinishedBuildings[
                Model.getRandomIntegerBetween(0, unfinishedBuildings.size() - 1)
        ]

        def tribe = me.role.tribe as NomadTribe
        def resources = tribe.resources

        me.actionQueue << new ShapeAction(Shape.FOLLOWER_BUILDER)
        me.actionQueue << new WaitAction(10)
        def wood = resources.findAll { it instanceof Wood }
        def stone = resources.findAll { it instanceof Stone }

        def neededWood = Model.buildingResources[Shape.HUT][Shape.WOOD]
        def neededStone = Model.buildingResources[Shape.HUT][Shape.STONE]

        def enoughWood = wood.size() >= neededWood
        def enoughStone = stone.size() >= neededStone

        //hur ska vi bygga detta enkelt?

        if (enoughWood && enoughStone) {

        } else {

        }
    }
}
