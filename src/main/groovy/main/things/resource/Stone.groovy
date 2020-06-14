package main.things.resource

import main.Model
import main.model.Tile

class Stone extends Resource {

    Stone (Tile tile) {
        super(tile)
        shape = SHAPE.STONE
    }

    @Override
    public String toString() {
        return "Stone {" +
                "id=" + id +
                '}';
    }
}
