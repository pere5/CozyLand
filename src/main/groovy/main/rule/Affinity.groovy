package main.rule

import main.Model
import main.calculator.RuleCalcs
import main.things.Drawable
import main.villager.Villager

class Affinity extends Rule {

    @Override
    int status(Villager me) {

        int withinRange = 0
        for (Villager villager: Model.villagers) {
            Double range = RuleCalcs.tileRange(villager, me)
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
            Double range = RuleCalcs.tileRange(villager, me)
            if (range < Villager.VISIBLE_ZONE_TILES) {
                targets << villager
            }
        }

        Double[] dest
        if (!targets) {
            dest = Model.generateTileXY(me, Villager.WALK_DISTANCE_TILES)
        } else {
            dest = RuleCalcs.centroid(targets)
        }

        stuff here lol
        me.tileQueue << [me.x, me.y] as Double[], dest, me)
    }

    @Override
    void toNewState(Villager villager) {
        villager.toPathfinderWorker()
    }
}
