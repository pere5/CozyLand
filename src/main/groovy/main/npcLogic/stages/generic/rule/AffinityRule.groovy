package main.npcLogic.stages.generic.rule

import main.Main
import main.Model
import main.calculator.Utility
import main.model.Tile
import main.model.Villager
import main.npcLogic.Rule
import main.npcLogic.action.ShapeAction
import main.npcLogic.action.WalkAction
import main.things.Drawable.Shape

class AffinityRule extends Rule {

    Closure walkActionClosure

    AffinityRule(int rank) {
        this.rank = rank
    }

    AffinityRule(int rank, Closure walkActionClosure) {
        this.rank = rank
        this.walkActionClosure = walkActionClosure
    }

    @Override
    int status(Villager me) {

        def tileNetwork = Model.tileNetwork as Tile[][]
        def (int tileX, int tileY) = me.getTileXY()

        int withinRange = 0
        Utility.getTilesWithinRadii(me, tileX, tileY, Main.COMFORT_ZONE_TILES) { int x, int y ->
            withinRange += tileNetwork[x][y].villagers.size()
        }

        if (withinRange == 0) {
            BAD
        } else if (withinRange >= 2 && withinRange <= 5) {
            GOOD
        } else if (withinRange >= 6) {
            GREAT
        } else {
            UNREACHABLE
        }
    }

    @Override
    void planWork(Villager me, int status) {
        def tileNetwork = Model.tileNetwork as Tile[][]
        def (int tileX, int tileY) = me.getTileXY()

        List<Villager> closeVillagers = []
        Utility.getTilesWithinRadii(me, tileX, tileY, Main.VISIBLE_ZONE_TILES) { int x, int y ->
            tileNetwork[x][y].villagers.each { Villager villager ->
                if (villager.id != me.id) {
                    closeVillagers << villager
                }
            }
        }

        int[] tileDest
        if (closeVillagers.size() == 0) {
            tileDest = Utility.closeRandomTile(me, me.tileXY, Main.WALK_DISTANCE_TILES_MAX, Main.WALK_DISTANCE_TILES_MIN)
        } else {
            tileDest = Utility.centroidTile(closeVillagers, me, Main.WALK_DISTANCE_TILES_MAX)
        }
        me.actionQueue << new ShapeAction(Shape.WARRIOR)
        me.actionQueue << new WalkAction(tileDest, walkActionClosure)
    }
}
