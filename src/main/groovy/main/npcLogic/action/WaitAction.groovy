package main.npcLogic.action

import main.model.Villager
import main.npcLogic.Action

class WaitAction extends Action {

    WaitAction(int waitSeconds) {
        this.waitSeconds = waitSeconds
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
        def resolution = waitForPeriod()
        return resolution
    }
}
