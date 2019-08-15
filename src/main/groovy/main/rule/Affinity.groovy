package main.rule

import main.Model
import main.model.Tile
import main.things.Drawable
import main.villager.Villager

class Affinity extends Rule {

    @Override
    int status(Villager me) {

        def tileNetwork = Model.tileNetwork as Tile[][]
        def (int tX, int tY) = me.getTile()

        int withinRange = 0
        calculateWithinRadii(tY, tX) { int x, int y ->
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

    //https://stackoverflow.com/questions/40779343/java-loop-through-all-pixels-in-a-2d-circle-with-center-x-y-and-radius?noredirect=1&lq=1
    private def calculateWithinRadii(int tY, int tX, Closure function) {
        int r = Villager.COMFORT_ZONE_TILES
        int r2 = r * r
        // iterate through all y-coordinates
        for (int y = tY - r; y <= tY + r; y++) {
            int di2 = (y - tY) * (y - tY)
            // iterate through all x-coordinates
            for (int x = tX - r; x <= tX + r; x++) {
                // test if in-circle
                if ((x - tX) * (x - tX) + di2 <= r2) {
                    //TestPrints.printRadii(x, y, me)
                    function(x, y)
                }
            }
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
