package main.model

import main.Model
import main.Model.TravelType
import main.action.Action
import main.action.WalkAction
import main.role.Role
import main.role.alone.AloneRole
import main.things.Drawable
import main.things.building.Building
import main.things.tool.Tool

class Villager extends Drawable {

    def metaObjects = [:]

    Queue<Action> actionQueue = new LinkedList<>()
    boolean ruleWorker
    boolean pathfinderWorker
    boolean workWorker

    Role role
    Building home
    Tool tool

    static Villager test() {
        def villager = new Villager()
        villager.role = new AloneRole()
        villager.setShape(Shape.WARRIOR)
        def (Double x, Double y) = Model.generateXY()
        villager.x = x
        villager.y = y
        villager.toRuleWorker()
        return villager
    }

    void toWorkWorker() {
        //println("${id}-w")
        pathfinderWorker = false
        ruleWorker = false
        workWorker = true
    }

    void toRuleWorker() {
        //println("${id}-r")
        pathfinderWorker = false
        ruleWorker = true
        workWorker = false
    }

    void toPathfinderWorker() {
        //println("${id}-p")
        pathfinderWorker = true
        ruleWorker = false
        workWorker = false
    }

    boolean canTravel(TravelType travelType) {
        travelType != TravelType.WATER
    }

    void interrupt() {
        def action = actionQueue.peek()
        if (action instanceof WalkAction) {
            def first = action.pathQueue.find()
            if (first) {
                action.pathQueue.clear()
                action.pathQueue << first
            }
        }
    }
}
