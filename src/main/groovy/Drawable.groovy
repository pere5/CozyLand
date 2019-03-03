import java.awt.Color
import java.util.concurrent.ThreadLocalRandom

class Drawable {

    Drawable() {
        this.id = Main.getNewId()
        if (x == 0 && y == 0) {
            this.x = generateX()
            this.y = generateY()
        }
    }

    static int generateX() {
        return Main.WINDOW_WIDTH / 2 + generateInt(Main.WINDOW_WIDTH / 3 as int)
    }

    static int generateY() {
        return Main.WINDOW_HEIGHT / 2 + generateInt(Main.WINDOW_HEIGHT / 3 as int)
    }

    static int generateInt(int distance) {
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
