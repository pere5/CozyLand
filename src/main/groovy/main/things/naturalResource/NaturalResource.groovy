package main.things.naturalResource

import main.Model
import main.model.Tile
import main.things.Drawable

abstract class NaturalResource extends Drawable {
    NaturalResource(Tile tile, Shape shape) {
        super()
        this.shape = shape
        this.image = Model.shadeImage(Model.shapeProperties[shape].image, tile.color)
        this.parent = tile.id
        this.x = tile.x
        this.y = tile.y
    }
}
