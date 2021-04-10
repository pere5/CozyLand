package main.things

import main.Model

import java.awt.*

class Artifact extends Drawable {

    Artifact () {
        size = 1
        color = Color.RED
        setShapeAndImage(Model.Shape.RECT)
    }
}
