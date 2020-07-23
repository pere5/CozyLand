package main.rule

import main.Main
import main.Model
import main.action.ShapeAction
import main.action.WalkAction
import main.model.Villager
import main.role.tribe.NomadTribe
import main.things.Drawable.Shape
import main.things.resource.Stone
import main.things.resource.Wood

class BuilderRule extends Rule {

    @Override
    int status(Villager me) {
        if (me.role.tribe.goodLocation) {
            BAD
        } else {
            GREAT
        }
    }

    @Override
    void planWork(Villager me, int status) {

        def tribe = me.role.tribe as NomadTribe
        def resources = tribe.resources

        if (Model.withinCircle(me.tileXY, tribe.shaman.tileXY, Main.NEXT_TO_TILES)) {

            me.actionQueue << new ShapeAction(Shape.FOLLOWER_BUILDER)

            def wood = resources.findAll { it instanceof Wood }
            def stone = resources.findAll { it instanceof Stone }

            def neededWood = Model.buildingMaterials[Shape.HUT][Shape.WOOD]
            def neededStone = Model.buildingMaterials[Shape.HUT][Shape.STONE]

            def enoughWood = wood.size() >= neededWood
            def enoughStone = stone.size() >= neededStone

            //hur ska vi bygga detta enkelt?

            if (enoughWood && enoughStone) {
                neededWood.times { def i -> resources.remove(wood[i]) }
                neededStone.times { def i -> resources.remove(stone[i]) }
            } else {

            }
        } else {
            def tileDest = Model.closeRandomTile(tribe.shaman, null, Main.NEXT_TO_TILES)
            me.actionQueue << new WalkAction(tileDest)
        }
    }
}
