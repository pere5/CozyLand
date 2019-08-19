package main.model

import main.Model
import main.Model.TravelType
import main.action.Action
import main.rule.Role
import main.rule.Rule
import main.rule.alive.Alive
import main.things.Drawable

import java.awt.*
import java.util.List
import java.util.Queue

class Villager extends Drawable {

    static int COMFORT_ZONE_TILES = 3
    static int VISIBLE_ZONE_TILES = 6
    static int WALK_DISTANCE_TILES = 9
    static int SHAMAN_DISTANCE_TILES = 13

    List<Rule> rules = []
    Queue<Action> actionQueue = new LinkedList<>()
    boolean ruleWorker
    boolean pathfinderWorker
    boolean workWorker

    Villager boss
    Role role

    static Villager test() {
        def villager = new Villager()
        villager.role = new Alive()
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
