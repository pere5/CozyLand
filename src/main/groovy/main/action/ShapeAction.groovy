package main.action


import main.model.Villager
import main.things.Drawable.Shape

class ShapeAction extends Action {

    Shape shape

    ShapeAction(Shape shape) {
        this.shape = shape
    }

    @Override
    void switchWorker(Villager villager) {
        throw new UnsupportedOperationException()
    }

    @Override
    boolean doIt(Villager villager) {
        villager.setShape(shape, villager.role.tribe)
        return DONE
    }
}
