package main.things.naturalResource

import main.Model
import main.model.Tile

class Tree extends NaturalResource {

    Tree (Tile tile, Model.Shape shape) {
        super(tile, shape)
    }

    @Override
    String toString() {
        return "Tree {" +
                "id=" + id +
                '}';
    }
}
