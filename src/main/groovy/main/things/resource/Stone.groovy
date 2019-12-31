package main.things.resource

import main.Model
import main.model.Tile

class Stone extends Resource {

    Stone (Tile tile) {
        super(tile)
        shape = SHAPES.STONE
        //image = Model.shadeImage(Model.stoneImage, tile.color)
        image = Model.stoneImage
    }

    @Override
    public String toString() {
        return "Stone {" +
                "id=" + id +
                '}';
    }
}
