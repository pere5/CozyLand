package main.npcLogic.action

import main.Model
import main.model.Villager
import main.npcLogic.Action

class ShapeAction extends Action {

    Model.Shape shape

    ShapeAction(Model.Shape shape) {
        this.shape = shape
    }

    @Override
    boolean interrupt() {
        return false
    }

    @Override
    void switchWorker(Villager villager) {
        throw new UnsupportedOperationException()
    }

    @Override
    Resolution work(Villager villager) {
        villager.setShapeAndImage(shape)
        return Resolution.DONE
    }
}
