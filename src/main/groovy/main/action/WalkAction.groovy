package main.action

import main.model.StraightPath
import main.model.Villager

class WalkAction extends Action {

    int[] tileDest

    //this is populated by pathfinderWorker
    Queue<StraightPath> pathQueue = new LinkedList<>()

    WalkAction(int[] tile) {
        tileDest = tile
    }

    @Override
    void switchWorker(Villager me) {
        me.toPathfinderWorker()
    }

    @Override
    boolean doIt(Villager me) {
        def straightPath = pathQueue.peek()
        if (straightPath) {
            def step = straightPath.path.poll()
            if (step) {
                def (Double x, Double y) = step
                me.x = x
                me.y = y
                return CONTINUE
            } else {
                pathQueue.poll()
                return doIt(me)
            }
        } else {
            return DONE
        }
    }
}
