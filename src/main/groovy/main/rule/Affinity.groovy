package main.rule

import main.Model
import main.exception.PerIsBorkenException
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
        } else if (withinRange >= 1 && withinRange <= 5) {
            GOOD
        } else if (withinRange >= 6) {
            GREAT
        } else {
            UNREACHABLE
        }
    }

    @Override
    void startWork(Villager me, int status) {
        List<Drawable> targets = []
        for (Villager villager: Model.villagers) {
            Double range = Model.tileRange(villager, me)
            if (range < Villager.VISIBLE_ZONE_TILES) {
                targets << villager
            }
        }

        Model.tileNetwork.length

        int[] dest
        if (true || !targets) {
            dest = Model.closeRandomTile(me, Villager.WALK_DISTANCE_TILES)
        } else {
            dest = Model.centroidTile(targets, me, Villager.WALK_DISTANCE_TILES)
        }

        if (Model.tileNetwork[dest[0]][dest[1]].travelType == Model.TravelType.WATER) {
            throw new PerIsBorkenException()
        }

        me.tileQueue << dest
    }

    @Override
    void toNewState(Villager villager) {
        villager.toPathfinderWorker()
    }
}
