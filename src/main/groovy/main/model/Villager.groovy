package main.model

import main.Model
import main.Model.TravelType
import main.action.Action
import main.role.BaseRole
import main.role.Role
import main.things.Drawable

import java.awt.image.BufferedImage

class Villager extends Drawable {

    Queue<Action> actionQueue = new LinkedList<>()
    boolean ruleWorker
    boolean pathfinderWorker
    boolean workWorker
    Role role

    static Villager test() {
        def villager = new Villager()
        villager.role = new BaseRole()
        villager.shape = SHAPE.WARRIOR
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
}
