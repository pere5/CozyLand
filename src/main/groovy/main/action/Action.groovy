package main.action


import main.things.Drawable

abstract class Action {

    static boolean CONTINUE = true
    static boolean DONE = false

    long iteration = 0

    abstract boolean doIt(Drawable me);


    fix this somehow

    void eachSecond (Double times, Closure closure) {
        if (iteration % interval == 0) {
            closure()
        }
        iteration++
    }
}
