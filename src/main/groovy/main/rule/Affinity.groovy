package main.rule

import main.Model
import main.villager.Villager

class Affinity extends Rule {

    @Override
    int status(Villager me) {
/*
        for (Villager villager: Model.villagers) {
            double range = villager != me ? range(villager, me): Double.MAX_VALUE;
            if (range < Const.COMFORT_ZONE) {
                result += 1;
            }
            if (range < Const.VISIBLE_ZONE) {
                me.addTarget(villager);
            }
        }
*/
        if (1) {
            BAD
        } else if (2) {
            GOOD
        } else if (3) {
            GREAT
        } else {
            UNREACHABLE
        }
    }

    @Override
    void startWork(Villager villager, int status) {
        def pixelDest = Model.generateTileXY()
        villager.tileQueue << pixelDest
    }

    @Override
    void stateWhenDone(Villager villager) {
        villager.toPathfinderWorker()
    }
}
