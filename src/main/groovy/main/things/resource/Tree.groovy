package main.things.resource

import main.model.Tile

import java.awt.*

class Tree extends Resource {

    Tree (Tile tile) {
        size = 10
        color = Color.GREEN
        shape = SHAPES.RECT
        parent = tile.id
        x = tile.x
        y = tile.y
    }

    @Override
    String toString() {
        return "Tree {" +
                "id=" + id +
                '}';
    }
}
