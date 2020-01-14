package main.action

import main.model.Villager

abstract class Action {

    long last = System.currentTimeMillis()

    static boolean CONTINUE = true
    static boolean DONE = false

    abstract boolean doIt(Villager me);

    void perTenSeconds(Double times, Closure closure) {
        long interval = 10000 / times
        long millis = System.currentTimeMillis() - last

        if (millis > interval) {
            closure()
            last = System.currentTimeMillis()
        }
    }
}
