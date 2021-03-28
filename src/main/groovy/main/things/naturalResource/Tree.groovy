package main.things.naturalResource

import main.Model
import main.model.Tile

class Tree extends NaturalResource {

    Tree (Tile tile) {
        super(tile, Model.Shape.TREE)
    }

    @Override
    String toString() {
        return "Tree {" +
                "id=" + id +
                '}';
    }
}
