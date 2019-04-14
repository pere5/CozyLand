package main.villager

import main.Model
import main.things.Drawable

import java.awt.*
import java.util.Queue

class Villager extends Drawable {

    Queue<Action> actionQueue = new LinkedList<>()

    Villager() {
        size = 3
        color = Color.BLUE
        def (x, y) = Model.generateXY()
        this.x = x
        this.y = y
    }

    boolean work() {
        actionQueue.poll()?.doIt(this)
    }
}
