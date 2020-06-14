package main.action

import main.Model
import main.model.Villager
import main.things.Drawable
import main.things.Drawable.SHAPE

import java.awt.image.BufferedImage

class ShapeAction extends Action {

    SHAPE shape

    ShapeAction(SHAPE shape) {
        this.shape = shape
    }

    @Override
    void switchWorker(Villager villager) {
        throw new UnsupportedOperationException()
    }

    @Override
    boolean doIt(Villager villager) {
        villager.shape = shape
        return DONE
    }
}
