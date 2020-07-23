package main.things.naturalResource


import main.model.Tile

class Tree extends NaturalResource {

    Tree (Tile tile) {
        super(tile, Shape.TREE)
    }

    @Override
    String toString() {
        return "Tree {" +
                "id=" + id +
                '}';
    }
}
