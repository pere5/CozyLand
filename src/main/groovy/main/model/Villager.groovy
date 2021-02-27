package main.model

import main.Model
import main.Model.TravelType
import main.npcLogic.Action
import main.npcLogic.Role
import main.npcLogic.stages.alone.role.AloneRole
import main.things.Drawable
import main.things.building.Building

import java.awt.Color

class Villager extends Drawable {

    def metaObjects = [:]

    Queue<Action> actionQueue = new LinkedList<>()
    boolean ruleWorker
    boolean pathfinderWorker
    boolean workWorker

    Tile tile
    Role role
    Building home

    static Villager test() {
        def villager = new Villager()
        Random rand = new Random()
        villager.testColor = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat())
        villager.role = new AloneRole()
        villager.setShape(Shape.WARRIOR)
        def (Double x, Double y) = Model.generateXY()
        villager.x = x
        villager.y = y
        Model.placeInTileNetwork(villager)
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
        actionQueue.clear()
        if (action?.interrupt()) {
            actionQueue << action
        }
    }
}
