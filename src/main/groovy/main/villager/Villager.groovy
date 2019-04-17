package main.villager

import main.Model
import main.things.Drawable

import java.awt.*
import java.util.Queue

class Villager extends Drawable {

    Queue<Action> actionQueue = new LinkedList<>()
    boolean ruleWorker
    boolean pathfinderWorker
    boolean workWorker

    Villager() {
        size = 3
        color = Color.BLUE
        def (x, y) = Model.generateXY()
        this.x = x
        this.y = y
        toRuleWorker()
    }

    boolean work() {
        def action = actionQueue.peek()
        if (action) {
            def canContinue = action.doIt(this)
            if (canContinue) {
                return Action.CONTINUE
            } else {
                actionQueue.poll()
                return Action.DONE
            }
        } else {
            return Action.DONE
        }
    }

    void toWorkWorker() {
        println("${id}-w")
        pathfinderWorker = false
        ruleWorker = false
        workWorker = true
    }

    void toRuleWorker() {
        println("${id}-r")
        pathfinderWorker = false
        ruleWorker = true
        workWorker = false
    }

    void toPathfinderWorker() {
        println("${id}-p")
        pathfinderWorker = true
        ruleWorker = false
        workWorker = false
    }
}
