package main.things.resource

import main.Model
import main.things.Drawable

abstract class Resource extends Drawable {
    Resource(Model.Shape shape) {
        setShapeAndImage(shape)
    }
}
