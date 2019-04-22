package main.things

import main.Model

import java.awt.*

class Stone extends Drawable {

    Stone () {
        size = 35
        color = Color.DARK_GRAY
        shape = SHAPES.CIRCLE
        def (double x, double y) = Model.generateXY()
        this.x = x
        this.y = y
    }
}
