package main.person

import main.things.Drawable

import java.awt.Color

class Person extends Drawable {

    Queue<Action> actionQueue = new LinkedList<>()

    Person() {
        size = 3
        color = Color.BLUE
        def start = generateXY()
        def path = [
                generateXY(),
                generateXY(),
                generateXY(),
                generateXY()
        ]
        actionQueue.add(new WalkPath(start, path))
    }

    def work() {
        if(!actionQueue.peek()?.doIt(this)) {
            actionQueue.poll()
        }
    }
}
