package main.person

import main.things.Drawable

import java.awt.Color

class Person extends Drawable {

    Queue<Action> actionQueue = new LinkedList<>()

    Person() {
        size = 3
        color = Color.BLUE
        def start = (double[])[generateX(), generateY()]
        def path = [
                (double[])[generateX(), generateY()], (double[])[generateX(), generateY()],
                (double[])[generateX(), generateY()], (double[])[generateX(), generateY()],
                (double[])[generateX(), generateY()], (double[])[generateX(), generateY()],
                (double[])[generateX(), generateY()], (double[])[generateX(), generateY()]
        ]
        actionQueue.add(new WalkPath(start, path))
    }

    def work() {
        if(!actionQueue.peek()?.doIt(this)) {
            actionQueue.poll()
        }
    }
}
