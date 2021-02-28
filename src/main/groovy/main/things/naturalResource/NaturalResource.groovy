package main.things.naturalResource

import main.Model
import main.calculator.ImageUtils
import main.model.Tile
import main.things.Drawable

abstract class NaturalResource extends Drawable {
    NaturalResource(Tile tile, Shape shape) {
        this.shape = shape
        this.image = ImageUtils.shadeImage(Model.shapeProperties[shape].image, tile.color)
        this.parent = tile.id
        this.x = tile.x
        this.y = tile.y
    }
}
