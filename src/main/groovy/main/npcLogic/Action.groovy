package main.npcLogic

import main.model.Villager

abstract class Action {

    static boolean CONTINUE = true
    static boolean DONE = false

    private final Integer DEFAULT_ID = 1
    private final Map<Integer, Long> lastMap = [:].withDefault { 0L }

    boolean initialized = true
    Closure closure

    Action () { }

    Action(Boolean initialized) {
        this.initialized = initialized
    }

    Action(Boolean initialized, Closure closure) {
        this.initialized = initialized
        this.closure = closure
    }

    abstract boolean interrupt()
    abstract void switchWorker(Villager me)
    abstract boolean doIt(Villager me)

    void perInterval(Long interval, Closure closure) {
        perInterval(interval, DEFAULT_ID, closure)
    }

    void perInterval(Long interval, Integer id, Closure closure) {
        Long last = lastMap[id]
        long rightNow = System.currentTimeMillis()
        long millis = rightNow - last
        if (millis > interval) {
            closure()
            lastMap[id] = rightNow
        }
    }
}
