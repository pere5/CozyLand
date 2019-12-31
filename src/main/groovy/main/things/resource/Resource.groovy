package main.things.resource

import main.model.Tile
import main.things.Drawable

abstract class Resource extends Drawable {
    Resource(Tile tile) {
        parent = tile.id
        x = tile.x
        y = tile.y
    }
}
