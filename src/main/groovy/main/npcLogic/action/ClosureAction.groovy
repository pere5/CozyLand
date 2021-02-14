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
    boolean doIt(Villager me) {
        closure()
        return DONE
    }
}
