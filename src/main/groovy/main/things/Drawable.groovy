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
        if (x == 0 && y == 0) {
            def (x, y) = generateXY()
            this.x = x
            this.y = y
        }
    }

    static double[] generateXY() {
        (double[])[
                Model.WINDOW_WIDTH / 2 + generate(Model.WINDOW_WIDTH / 3 as int),
                Model.WINDOW_HEIGHT / 2 + generate(Model.WINDOW_HEIGHT / 3 as int)
        ]
    }

    static double generate(int distance) {
        return distance - ThreadLocalRandom.current().nextInt(0, distance * 2 + 1)
    }

    int getX() {
        x
    }
    int getY() {
        y
    }
}
