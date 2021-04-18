package main.npcLogic.action

import main.model.Villager
import main.npcLogic.Action

class ClosureAction extends Action {

    Closure closure

    ClosureAction(Closure closure) {
        this.closure = closure
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
        closure()
        return Resolution.DONE
    }
}
