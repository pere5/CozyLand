package main.things.naturalResource

import main.Model
import main.model.Tile
import main.things.Drawable
import main.utility.ImageUtils

abstract class NaturalResource extends Drawable {
    NaturalResource(Tile tile, Shape shape) {
        this.shape = shape
        this.image = ImageUtils.shadeImage(Model.shapeProperties[shape], tile.color)
        this.parent = tile.id
        this.x = tile.x
        this.y = tile.y
    }
}
