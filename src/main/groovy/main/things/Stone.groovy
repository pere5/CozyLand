package main.things

import main.Model

import java.awt.Color

class Stone extends Drawable {

    Stone () {
        size = 35
        color = Color.DARK_GRAY
        shape = SHAPES.CIRCLE
        def (x, y) = Model.generateXY()
        this.x = x
        this.y = y
    }

    def work() {}
}
