package main.action


import main.things.Drawable

abstract class Action {

    long last = System.currentTimeMillis()

    static boolean CONTINUE = true
    static boolean DONE = false

    abstract boolean doIt(Drawable me);

    void timesPerTenSeconds(Double times, Closure closure) {
        long interval = 10000 / times
        long millis = System.currentTimeMillis() - last

        if (millis > interval) {
            closure()
            last = System.currentTimeMillis()
        }
    }
}
