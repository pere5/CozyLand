package main.things.resource

import main.Model
import main.model.Tile

class Tree extends Resource {

    Tree (Tile tile) {
        super(tile)
        shape = SHAPES.TREE
        //image = Model.shadeImage(Model.treeImage, tile.color)
        image = Model.treeImage
    }

    @Override
    String toString() {
        return "Tree {" +
                "id=" + id +
                '}';
    }
}
