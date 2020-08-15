package main.rule

import main.Main
import main.Model
import main.action.ShapeAction
import main.action.WaitAction
import main.action.WalkAction
import main.model.Villager
import main.role.tribe.NomadTribe
import main.things.Drawable.Shape
import main.things.resource.Stone
import main.things.resource.Wood

class BuilderRule extends Rule {

    @Override
    int status(Villager me) {
        def enoughResourcesToBuild = false
        if (!me.home && me.role.tribe.goodLocation && enoughResourcesToBuild) {
            BAD
        } else {
            GREAT
        }
    }

    @Override
    void planWork(Villager me, int status) {

        def tribe = me.role.tribe as NomadTribe
        def resources = tribe.resources

        if (Model.withinCircle(me.tileXY, tribe.shaman.tileXY, Main.COMFORT_ZONE_TILES)) {

            me.actionQueue << new ShapeAction(Shape.FOLLOWER_BUILDER)

            /*
                - Hitta en färdig Hut som ingen bor i
                    - Ta den som ditt Home
                - !^ Hitta en oklar Hut har plats för fler byggare
                    - Bygg på den
                - !^ Anlägg en ny Hut
                    - Bygg på den
             */



            def wood = resources.findAll { it instanceof Wood }
            def stone = resources.findAll { it instanceof Stone }

            def neededWood = Model.buildingMaterials[Shape.HUT][Shape.WOOD]
            def neededStone = Model.buildingMaterials[Shape.HUT][Shape.STONE]

            def enoughWood = wood.size() >= neededWood
            def enoughStone = stone.size() >= neededStone

            //hur ska vi bygga detta enkelt?

            if (enoughWood && enoughStone) {

            } else {

            }
        } else {
            def tileDest = Model.closeRandomTile(me, tribe.shaman.tileXY, Main.COMFORT_ZONE_TILES)
            me.actionQueue << new WalkAction(tileDest)
            me.actionQueue << new WaitAction(2)
        }
    }
}
