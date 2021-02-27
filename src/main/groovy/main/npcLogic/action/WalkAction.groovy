package main.npcLogic.action

import main.Model
import main.exception.PerIsBorkenException
import main.model.StraightPath
import main.model.Tile
import main.model.Villager
import main.npcLogic.Action

class WalkAction extends Action {

    int[] tileDest

    //this is populated by pathfinderWorker
    Queue<StraightPath> pathQueue = new LinkedList<>()

    WalkAction(int[] tile) {
        super(false)
        tileDest = tile
    }

    WalkAction(int[] tile, Closure closure) {
        super(false, closure)
        tileDest = tile
    }

    @Override
    boolean interrupt() {
        def first = pathQueue.find()
        if (first) {
            pathQueue.clear()
            pathQueue << first
            return true
        } else {
            return false
        }
    }

    @Override
    void switchWorker(Villager villager) {
        villager.toPathfinderWorker()
    }

    @Override
    boolean doIt(Villager villager) {
        Boolean result
        def straightPath = pathQueue.peek()
        if (straightPath) {
            def step = straightPath.path.poll()
            if (step) {
                def (Double x, Double y) = step
                villager.x = x
                villager.y = y
                result = CONTINUE
            } else {
                pathQueue.poll()
                result = doIt(villager)
            }
        } else {
            result = DONE
        }

        if (result == DONE) {
            Model.placeInTileNetwork(villager)
        } else {
            perInterval (1000, 1) {
                Model.placeInTileNetwork(villager)
            }
        }

        if (closure) {
            perInterval(1000, 2) {
                closure(villager)
            }
        }

        return result
    }
}
