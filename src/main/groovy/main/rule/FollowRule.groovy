package main.rule

import main.Main
import main.Model
import main.action.WalkAction
import main.model.Villager
import main.role.tribe.NomadTribe

class FollowRule extends Rule {

    @Override
    int status(Villager me) {
        BAD
    }

    @Override
    void planWork(Villager me, int status) {
        me.actionQueue << new WalkAction(Model.closeRandomTile((me.role.tribe as NomadTribe).shaman, Main.COMFORT_ZONE_TILES))
    }

    @Override
    void toNewState(Villager villager) {
        villager.toPathfinderWorker()
    }
}

