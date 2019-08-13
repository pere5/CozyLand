package main.rule

import main.Model
import main.model.Tile
import main.things.Drawable
import main.villager.Villager

class Affinity extends Rule {

    @Override
    int status(Villager me) {

        int withinRange = 0
        def tileNetwork = Model.tileNetwork as Tile[][]
        def (int tX, int tY) = me.getTile()

        //Double range = Model.tileRange(villager, me)

        //https://stackoverflow.com/questions/40779343/java-loop-through-all-pixels-in-a-2d-circle-with-center-x-y-and-radius?noredirect=1&lq=1
        int r = Villager.COMFORT_ZONE_TILES
        int r2 = r*r;
        // iterate through all x-coordinates
        for (int x = tY - r; x <= tY + r; x++) {
            int di2 = (x - tY) * (x - tY)
            // iterate through all y-coordinates
            for (int y = tX - r; y <= tX + r; y++) {
                // test if in-circle
                if ((y - tX) * (y - tX) + di2 <= r2) {
                    withinRange += tileNetwork[x][y].villagers.size()
                }
            }
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
    void startWork(Villager me, int status) {
        List<Drawable> closeVillagers = [me]
        for (Villager villager: Model.villagers) {
            Double range = Model.tileRange(villager, me)
            if (range < Villager.VISIBLE_ZONE_TILES) {
                closeVillagers << villager
            }
        }

        int[] dest
        if (closeVillagers.size() == 1) {
            dest = Model.closeRandomTile(me, Villager.WALK_DISTANCE_TILES)
        } else {
            dest = Model.centroidTile(closeVillagers, me, Villager.WALK_DISTANCE_TILES)
        }

        me.tileQueue << dest
    }

    @Override
    void toNewState(Villager villager) {
        villager.toPathfinderWorker()
    }
}
