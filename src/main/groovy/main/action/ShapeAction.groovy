package main.action


import main.model.Villager
import main.things.Drawable.Shape

class ShapeAction extends Action {

    Shape shape

    ShapeAction(Shape shape) {
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
    boolean doIt(Villager villager) {
        villager.setShape(shape)
        return DONE
    }
}
