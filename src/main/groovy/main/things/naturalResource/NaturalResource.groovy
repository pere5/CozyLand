package main.things.naturalResource

import main.Model
import main.model.Tile
import main.things.Drawable

abstract class NaturalResource extends Drawable {
    NaturalResource(Tile tile, Model.Shape shape) {
        this.shape = shape
        this.parent = tile.id
        this.x = tile.x
        this.y = tile.y
    }
}
