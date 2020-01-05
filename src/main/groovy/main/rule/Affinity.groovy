package main.rule

import main.Main
import main.Model
import main.action.PathfinderAction
import main.model.Tile
import main.model.Villager
import main.things.Drawable

class Affinity extends Rule {

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
        List<Drawable> closeVillagers = []
        for (Villager villager: Model.villagers) {
            if (me.id != villager.id) {
                Double range = Model.tileRange(villager, me)
                if (range < Main.VISIBLE_ZONE_TILES) {
                    closeVillagers << villager
                }
            }
        }

        int[] dest
        if (closeVillagers.size() == 0) {
            dest = Model.closeRandomTile(me, Main.WALK_DISTANCE_TILES)
        } else {
            dest = Model.centroidTile(closeVillagers, me, Main.WALK_DISTANCE_TILES)
        }

        me.actionQueue << new PathfinderAction(dest)
    }

    @Override
    void toNewState(Villager villager) {
        villager.toPathfinderWorker()
    }
}
