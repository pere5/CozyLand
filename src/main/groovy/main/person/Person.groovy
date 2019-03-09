package main.person


import main.things.Drawable

import java.awt.Color

class Person extends Drawable {

    Queue<Action> actionQueue = new LinkedList<>()

    Person() {
        size = 3
        color = Color.BLUE
        def (x, y) = generateXY()
        this.x = x
        this.y = y
    }

    def work() {

        if (!actionQueue) {
            actionQueue.add(new WalkPath((double[])[x, y], generateXY()))
        }

        if(!actionQueue.peek()?.doIt(this)) {
            actionQueue.poll()
        }
    }
}
