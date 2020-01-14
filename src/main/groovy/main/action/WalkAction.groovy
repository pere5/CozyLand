package main.action

import main.model.StraightPath
import main.things.Drawable

class WalkAction extends Action {

    int[] tileDest

    //this is populated by pathfinderWorker
    Queue<StraightPath> pathQueue = new LinkedList<>()

    WalkAction(int[] tile) {
        tileDest = tile
    }

    @Override
    boolean doIt(Drawable drawable) {
        def straightPath = pathQueue.peek()
        if (straightPath) {
            def step = straightPath.path.poll()
            if (step) {
                def (Double x, Double y) = step
                drawable.x = x
                drawable.y = y
                return CONTINUE
            } else {
                pathQueue.poll()
                return doIt(drawable)
            }
        } else {
            return DONE
        }
    }
}