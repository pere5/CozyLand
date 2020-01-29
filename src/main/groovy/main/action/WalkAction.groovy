package main.action

import main.model.StraightPath
import main.model.Villager

class WalkAction extends Action {

    int[] tileDest

    //this is populated by pathfinderWorker
    Queue<StraightPath> pathQueue = new LinkedList<>()

    WalkAction(int[] tile) {
        this.initialized = false
        tileDest = tile
    }

    @Override
    void switchWorker(Villager villager) {
        villager.toPathfinderWorker()
    }

    @Override
    boolean doIt(Villager villager) {
        def straightPath = pathQueue.peek()
        if (straightPath) {
            def step = straightPath.path.poll()
            if (step) {
                def (Double x, Double y) = step
                villager.x = x
                villager.y = y
                return CONTINUE
            } else {
                pathQueue.poll()
                return doIt(villager)
            }
        } else {
            return DONE
        }
    }
}
