package main.action

import main.things.Drawable
import org.joda.time.LocalDateTime

class Wait extends Action {

    Date time

    Wait () {}

    @Override
    boolean doIt(Drawable me) {
        if (!time) {
            time = LocalDateTime.now().plusSeconds(5).toDate()
        }

        def resolution = time > new Date() ? CONTINUE : DONE
        return resolution
    }
}
