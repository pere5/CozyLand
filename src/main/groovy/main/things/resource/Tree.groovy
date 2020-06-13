package main.things.resource

import main.Model
import main.model.Tile

class Tree extends Resource {

    Tree (Tile tile) {
        super(tile)
        shape = SHAPE.TREE
        image = Model.shadeImage(Model.shapeProperties[SHAPE.TREE].image, tile.color)
    }

    @Override
    String toString() {
        return "Tree {" +
                "id=" + id +
                '}';
    }
}
