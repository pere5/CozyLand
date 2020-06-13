package main.action

import main.Model
import main.model.StraightPath
import main.model.Villager
import main.things.Drawable

import java.awt.image.BufferedImage

class ShapeAction extends Action {

    Drawable.SHAPES shape
    BufferedImage image

    ShapeAction(Drawable.SHAPES shape, BufferedImage image) {
        this.shape = shape
        this.image = image
    }

    @Override
    void switchWorker(Villager villager) {
        throw new UnsupportedOperationException()
    }

    @Override
    boolean doIt(Villager villager) {
        villager.shape = shape
        villager.image = image
        return DONE
    }
}
