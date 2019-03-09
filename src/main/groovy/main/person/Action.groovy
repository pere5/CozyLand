package main.person

import main.things.Drawable

abstract class Action {

    static boolean CONTINUE = true;
    static boolean DONE = false;

    abstract boolean doIt(Drawable me);
}
