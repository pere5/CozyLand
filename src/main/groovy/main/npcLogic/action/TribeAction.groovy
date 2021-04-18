package main.npcLogic.action

import main.model.Villager
import main.npcLogic.Action
import main.npcLogic.Tribe
import main.utility.GameUtils

class TribeAction extends Action {

    Tribe oldTribe
    Tribe newTribe

    TribeAction (Tribe oldTribe, Tribe newTribe) {
        this.oldTribe = oldTribe
        this.newTribe = newTribe
    }

    @Override
    boolean interrupt() {
        return false
    }

    @Override
    void switchWorker(Villager me) {
        throw new UnsupportedOperationException()
    }

    @Override
    Resolution work(Villager me) {
        GameUtils.transformTribe(oldTribe, newTribe)
        return Resolution.DONE
    }
}
