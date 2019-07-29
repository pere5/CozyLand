package main.rule

import main.Model
import main.things.Drawable
import main.villager.Villager

class Affinity extends Rule {

    @Override
    int status(Villager me) {

        int withinRange = 0
        for (Villager villager: Model.villagers) {
            Double range = Model.tileRange(villager, me)
            if (range < Villager.COMFORT_ZONE_TILES) {
                withinRange++
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
