package main.model

import main.Model
import main.Model.TravelType
import main.action.Action
import main.role.Base
import main.role.Role
import main.rule.Rule
import main.things.Drawable

import java.awt.*
import java.util.List
import java.util.Queue

class Villager extends Drawable {

    static int COMFORT_ZONE_TILES = 4
    static int VISIBLE_ZONE_TILES = 20
    static int WALK_DISTANCE_TILES = 13
    static int SHAMAN_DISTANCE_TILES = 15

    List<Rule> rules = []
    Queue<Action> actionQueue = new LinkedList<>()
    boolean ruleWorker
    boolean pathfinderWorker
    boolean workWorker

    Villager boss
    Role role

    static Villager test() {
        def villager = new Villager()
        villager.role = new Base()
        villager.rules.addAll(villager.role.subjectRules)
        villager.size = 4
        villager.color = Color.BLUE
        def (Double x, Double y) = Model.generateXY()
        villager.x = x
        villager.y = y
        villager.toRuleWorker()
        return villager
    }

    boolean work() {
        def action = actionQueue.peek()
        if (action) {
            def canContinue = action.doIt(this)
            if (canContinue) {
                return Action.CONTINUE
            } else {
                actionQueue.poll()
                if (actionQueue.peek()) {
                    return Action.CONTINUE
                } else {
                    return Action.DONE
                }
            }
        } else {
            return Action.DONE
        }
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