package main.npcLogic.action

import main.model.Villager
import main.npcLogic.Action

class WaitAction extends Action {

    int seconds
    long time

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
        if (!time) {
            time = System.currentTimeMillis() + (seconds * 1000)
        }

        def resolution = time > System.currentTimeMillis() ? CONTINUE : DONE
        return resolution
    }
}
