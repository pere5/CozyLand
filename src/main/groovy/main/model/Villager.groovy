package main.model

import main.Model
import main.Model.TravelType
import main.action.Action
import main.role.Base
import main.role.Role
import main.things.Drawable

class Villager extends Drawable {

    static int COMFORT_ZONE_TILES = 4
    static int VISIBLE_ZONE_TILES = 12
    static int WALK_DISTANCE_TILES = 12
    static int SHAMAN_DISTANCE_TILES = 15

    Queue<Action> actionQueue = new LinkedList<>()
    boolean ruleWorker
    boolean pathfinderWorker
    boolean workWorker
    Role role

    static Villager test() {
        def villager = new Villager()
        villager.role = new Base()
        villager.shape = SHAPES.BASE
        villager.image = Model.baseImage
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
