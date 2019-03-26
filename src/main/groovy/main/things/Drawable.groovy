package main.things

import main.Model

import java.awt.Color
import java.util.concurrent.ThreadLocalRandom
import main.Main

abstract class Drawable {

    enum SHAPES {
        RECT, CIRCLE
    }

    int id
    Color color = Color.BLACK
    SHAPES shape = SHAPES.RECT
    int size = 10
    double x = 0
    double y = 0

    Drawable() {
        this.id = Model.getNewId()
    }

    int getX() {
        x
    }
    int getY() {
        y
    }
}
