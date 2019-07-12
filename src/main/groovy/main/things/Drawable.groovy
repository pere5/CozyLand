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
}
