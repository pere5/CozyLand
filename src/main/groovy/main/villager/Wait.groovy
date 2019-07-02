package main.villager

import main.things.Drawable
import org.joda.time.LocalDateTime

class Wait extends Action {

    Date time

    Wait () {
        time = LocalDateTime.now().plusSeconds(20).toDate()
    }

    @Override
    boolean doIt(Drawable me) {
        def resolution = time > new Date() ? CONTINUE : DONE
        return resolution
    }
}
