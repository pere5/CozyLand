package main.rule

import main.Main
import main.Model
import main.action.WalkAction
import main.exception.PerIsBorkenException
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
        def tileDest = Model.closeRandomTile(shaman, null, Main.COMFORT_ZONE_TILES)
        me.actionQueue << new WalkAction(tileDest)
    }

    @Override
    void toNewState(Villager villager) {
        villager.toPathfinderWorker()
    }
}

