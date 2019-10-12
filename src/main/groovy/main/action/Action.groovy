package main.action

import groovy.time.TimeCategory
import main.things.Drawable

abstract class Action {

    Date last = new Date()

    static boolean CONTINUE = true
    static boolean DONE = false

    abstract boolean doIt(Drawable me);

    void timesPerTenSeconds(Double times, Closure closure) {
        int interval = 10000 / times
        def millis = TimeCategory.minus(new Date(), last).toMilliseconds()

        if (millis > interval) {
            closure()
            last = new Date()
        }
    }
}
