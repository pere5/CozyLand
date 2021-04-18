package main.npcLogic

import main.model.Villager

abstract class Action {

    enum Resolution { DONE, CONTINUE }

    private final Integer DEFAULT_ID = 1
    private final Map<Integer, Long> lastMap = [:].withDefault { 0L }

    boolean initializeByAnotherWorker = false
    Long timer
    Boolean suspend = Boolean.FALSE
    Closure closure

    Integer waitSeconds
    Long waitTime

    Action () {
        this.timer = System.currentTimeMillis()
    }

    Action(Boolean initializeByAnotherWorker) {
        this.timer = System.currentTimeMillis()
        this.initializeByAnotherWorker = initializeByAnotherWorker
    }

    Action(Boolean initializeByAnotherWorker, Closure closure) {
        this.timer = System.currentTimeMillis()
        this.initializeByAnotherWorker = initializeByAnotherWorker
        this.closure = closure
    }

    abstract boolean interrupt()
    abstract void switchWorker(Villager me)
    abstract Resolution doIt(Villager me)

    Resolution waitForPeriod() {
        if (!waitTime) {
            waitTime = System.currentTimeMillis() + (this.waitSeconds * 1000)
        }

        def resolution = waitTime > System.currentTimeMillis() ? Resolution.CONTINUE : Resolution.DONE
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
