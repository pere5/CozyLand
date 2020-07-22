package main.rule

import main.Main
import main.Model
import main.action.WalkAction
import main.exception.PerIsBorkenException
import main.model.Tile
import main.model.Villager
import main.things.Drawable

class AffinityRule extends Rule {

    @Override
    int status(Villager me) {

        def tileNetwork = Model.tileNetwork as Tile[][]
        def (int tileX, int tileY) = me.getTileXY()

        int withinRange = 0
        Model.getTilesWithinRadii(tileX, tileY, Main.COMFORT_ZONE_TILES) { int x, int y ->
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
        Model.getTilesWithinRadii(tileX, tileY, Main.VISIBLE_ZONE_TILES) { int x, int y ->
            tileNetwork[x][y].villagers.each { Villager villager ->
                if (villager.id != me.id) {
                    closeVillagers << villager
                }
            }
        }

        int[] tileDest
        if (closeVillagers.size() == 0) {
            tileDest = Model.closeRandomTile(me, Main.WALK_DISTANCE_TILES_MIN, Main.WALK_DISTANCE_TILES_MAX)
        } else {
            tileDest = Model.centroidTile(closeVillagers, me, Main.WALK_DISTANCE_TILES_MAX)
        }
        me.actionQueue << new WalkAction(tileDest)
    }

    @Override
    void toNewState(Villager villager) {
        villager.toPathfinderWorker()
    }
}
