package main.things

import java.awt.Color
import java.util.concurrent.ThreadLocalRandom
import main.Main

abstract class Drawable {

    Drawable() {
        this.id = Main.getNewId()
        if (x == 0 && y == 0) {
            this.x = generateX()
            this.y = generateY()
        }
    }

    static double generateX() {
        return Main.WINDOW_WIDTH / 2 + generate(Main.WINDOW_WIDTH / 3 as int)
    }

    static double generateY() {
        return Main.WINDOW_HEIGHT / 2 + generate(Main.WINDOW_HEIGHT / 3 as int)
    }

    static double generate(int distance) {
        return distance - ThreadLocalRandom.current().nextInt(0, distance * 2 + 1)
    }

    enum SHAPES {
        RECT, CIRCLE
    }

    int id
    Color color = Color.BLACK
    SHAPES shape = SHAPES.RECT
    int size = 10
    double x = 0
    double y = 0

    int getX() {
        x
    }
    int getY() {
        y
    }
}
