package main.things.resource

import main.model.Tile

import java.awt.*

class Stone extends Resource {

    Stone (Tile tile) {
        size = 2
        color = Color.BLACK
        shape = SHAPES.RECT
        parent = tile.id
        x = tile.x
        y = tile.y
    }

    @Override
    public String toString() {
        return "Stone {" +
                "id=" + id +
                '}';
    }
}
