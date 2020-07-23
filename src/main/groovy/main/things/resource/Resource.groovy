package main.things.resource

import main.Model
import main.things.Drawable

import java.awt.image.BufferedImage

abstract class Resource extends Drawable {
    Resource(Shape shape) {
        super()
        this.shape = shape
        this.image = Model.shapeProperties[shape].image as BufferedImage
    }
}
