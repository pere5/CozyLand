package main.action

import main.things.Drawable

class WaitAction extends Action {

    int seconds
    long time

    WaitAction(int seconds) {
        this.seconds = seconds
    }

    @Override
    boolean doIt(Drawable me) {
        if (!time) {
            time = System.currentTimeMillis() + (seconds * 1000)
        }

        def resolution = time > System.currentTimeMillis() ? CONTINUE : DONE
        return resolution
    }
}
