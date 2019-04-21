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
    SHAPES shape = SHAPES.RECT
    int size = 10
    double x = 0
    double y = 0

    Drawable() {
        this.id = Model.getNewId()
    }
}
