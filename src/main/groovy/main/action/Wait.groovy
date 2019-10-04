package main.action

import main.things.Drawable
import org.joda.time.LocalDateTime

class Wait extends Action {

    int seconds
    Date time

    Wait (int seconds) {
        this.seconds = seconds
    }

    @Override
    boolean doIt(Drawable me) {
        if (!time) {
            time = LocalDateTime.now().plusSeconds(seconds).toDate()
        }

        def resolution = time > new Date() ? CONTINUE : DONE
        return resolution
    }
}
