package main.things.resource

import main.Model
import main.things.Drawable

abstract class Resource extends Drawable {
    Resource(Shape shape) {
        this.shape = shape
        this.image = Model.shapeImageMap[shape]
    }
}
