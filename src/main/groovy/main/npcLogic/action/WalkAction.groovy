package main.npcLogic.action

import main.model.StraightPath
import main.model.Villager
import main.npcLogic.Action
import main.utility.Utility

class WalkAction extends Action {

    int[] tileDest

    //this is populated by pathfinderWorker
    Queue<StraightPath> pathQueue = new LinkedList<>()

    WalkAction(int[] tile) {
        super(true)
        tileDest = tile
    }

    WalkAction(int[] tile, Closure closure) {
        super(true, closure)
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
    Resolution work(Villager villager) {
        Resolution result
        def straightPath = pathQueue.peek()
        if (straightPath) {
            def step = straightPath.path.poll()
            if (step) {
                def (Double x, Double y) = step
                villager.x = x
                villager.y = y
                result = Resolution.CONTINUE
            } else {
                pathQueue.poll()
                result = work(villager)
            }
        } else {
            result = Resolution.DONE
        }

        if (result == Resolution.DONE) {
            Utility.placeInTileNetwork(villager)
        } else {
            perInterval (1000, 1) {
                Utility.placeInTileNetwork(villager)
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
