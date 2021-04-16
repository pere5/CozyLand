package main.npcLogic.action

import main.model.Villager
import main.npcLogic.Action

class WaitAction extends Action {

    WaitAction(int seconds) {
        this.seconds = seconds
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
    boolean doIt(Villager me) {
        def resolution = waitForPeriod()
        return resolution
    }
}
