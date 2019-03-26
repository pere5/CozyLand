package main.things

import main.Model

import java.awt.Color

class Tree extends Drawable {

    Tree () {
        size = 12
        color = Color.GREEN
        shape = SHAPES.CIRCLE
        def (x, y) = Model.generateXY()
        this.x = x
        this.y = y
    }

    def work() {}
}
