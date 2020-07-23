package main.things.resource

import main.Model
import main.model.Tile

class Rock extends NaturalResource {

    Rock(Tile tile) {
        super(tile)
        shape = Shape.STONE
        image = Model.shadeImage(Model.shapeProperties[shape].image, tile.color)
    }

    @Override
    public String toString() {
        return "Stone {" +
                "id=" + id +
                '}';
    }
}
