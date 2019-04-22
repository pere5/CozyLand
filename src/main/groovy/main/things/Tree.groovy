package main.things

import main.Model

import java.awt.*

class Tree extends Drawable {

    Tree () {
        size = 12
        color = Color.GREEN
        shape = SHAPES.CIRCLE
        def (double x, double y) = Model.generateXY()
        this.x = x
        this.y = y
    }
}
