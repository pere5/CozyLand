package main.things.resource

import main.Model
import main.model.Tile

class Stone extends Resource {

    Stone (Tile tile) {
        super(tile)
        shape = SHAPE.STONE
        image = Model.shadeImage(Model.shapeProperties[SHAPE.STONE].image, tile.color)
    }

    @Override
    public String toString() {
        return "Stone {" +
                "id=" + id +
                '}';
    }
}
