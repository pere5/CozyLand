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
        def shaman = (me.role.tribe as NomadTribe).shaman
        me.actionQueue << new WalkAction(Model.closeRandomTile(shaman, null, Main.COMFORT_ZONE_TILES))
    }

    @Override
    void toNewState(Villager villager) {
        villager.toPathfinderWorker()
    }
}

