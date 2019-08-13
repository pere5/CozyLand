package main.things

import main.Model

import java.awt.*

abstract class Drawable {

    enum SHAPES {
        RECT, CIRCLE
    }

    int id
    int parent
    Color color
    Color testColor
    SHAPES shape = SHAPES.RECT
    int size = 10
    Double x = 0
    Double y = 0

    Drawable() {
        this.id = Model.getNewId()
    }

    int[] getTile() {
        Model.pixelToTileIdx(x, y)
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof Drawable)) return false

        Drawable drawable = (Drawable) o

        if (id != drawable.id) return false

        return true
    }

    int hashCode() {
        return id
    }
}
