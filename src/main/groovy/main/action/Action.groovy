package main.action

import main.model.Villager

abstract class Action {

    static boolean CONTINUE = true
    static boolean DONE = false

    long last = System.currentTimeMillis()
    boolean initialized = true

    abstract void switchWorker(Villager me)
    abstract boolean doIt(Villager me)

    void perTenSeconds(Double times, Closure closure) {
        long interval = 10000 / times
        long millis = System.currentTimeMillis() - last

        if (millis > interval) {
            closure()
            last = System.currentTimeMillis()
        }
    }
}
