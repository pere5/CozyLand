package main.villager

import main.Model
import main.things.Drawable

import java.awt.*
import java.util.Queue

class Villager extends Drawable {

    Queue<Action> actionQueue = new LinkedList<>()
    boolean lookingForRule
    boolean planningPath
    boolean working

    Villager() {
        size = 3
        color = Color.BLUE
        def (x, y) = Model.generateXY()
        this.x = x
        this.y = y
        inLookingForRule()
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

    void inWorking() {
        planningPath = false
        lookingForRule = false
        working = true
    }

    void inLookingForRule() {
        planningPath = false
        lookingForRule = true
        working = false
    }

    void inPlanningPath() {
        planningPath = true
        lookingForRule = false
        working = false
    }
}
