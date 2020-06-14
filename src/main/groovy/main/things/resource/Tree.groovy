package main.things.resource

import main.Model
import main.model.Tile

class Tree extends Resource {

    Tree (Tile tile) {
        super(tile)
        shape = SHAPE.TREE
    }

    @Override
    String toString() {
        return "Tree {" +
                "id=" + id +
                '}';
    }
}
