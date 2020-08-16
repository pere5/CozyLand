package main.action

import main.model.Villager

class ClosureAction extends Action {

    Closure closure

    ClosureAction(Closure closure) {
        this.closure = closure
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
