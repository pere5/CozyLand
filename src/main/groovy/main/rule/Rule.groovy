package main.rule

import main.villager.Villager

abstract class Rule {

    Rule (int rank) {
        this.rank = rank
    }

    static int UNREACHABLE = 3
    static int GREAT = 2
    static int GOOD = 1
    static int BAD = 0

    int rank
    abstract int status(Villager villager)
    abstract void startWork(Villager villager, int status)
    abstract void toNewState(Villager villager)
}