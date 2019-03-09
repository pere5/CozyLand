package main.person

import main.things.Drawable

import java.awt.Color

class Person extends Drawable {

    Queue<Action> actionQueue = new LinkedList<>()

    Person() {
        size = 3
        color = Color.BLUE
        actionQueue.add(new WalkPath((double[])[generateX(), generateY()], (double[])[generateX(), generateY()]))
    }

    def work() {
        if(!actionQueue.peek()?.doIt(this)) {
            actionQueue.poll()
        }
    }
}
