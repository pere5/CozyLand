package main.npcLogic

import main.model.Villager

abstract class Action {

    static boolean CONTINUE = true
    static boolean DONE = false

    boolean resolution = CONTINUE

    private final Integer DEFAULT_ID = 1
    private final Map<Integer, Long> lastMap = [:].withDefault { 0L }

    boolean initializeByAnotherWorker = false
    boolean timerStarted = false
    Long timer
    Closure closure

    Integer waitSeconds
    Long waitTime

    Action () { }

    Action(Boolean initializeByAnotherWorker) {
        this.initializeByAnotherWorker = initializeByAnotherWorker
    }

    Action(Boolean initializeByAnotherWorker, Closure closure) {
        this.initializeByAnotherWorker = initializeByAnotherWorker
        this.closure = closure
    }

    abstract boolean interrupt()
    abstract void switchWorker(Villager me)
    abstract boolean doIt(Villager me)

    boolean waitForPeriod() {
        if (!waitTime) {
            waitTime = System.currentTimeMillis() + (this.waitSeconds * 1000)
        }

        def resolution = waitTime > System.currentTimeMillis() ? CONTINUE : DONE
        return resolution
    }

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
