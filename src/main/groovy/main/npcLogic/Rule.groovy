package main.npcLogic

import main.model.Villager

abstract class Rule {

    static int UNREACHABLE = 3
    static int GREAT = 2
    static int GOOD = 1
    static int BAD = 0

    int rank
    abstract int status(Villager villager)
    abstract void planWork(Villager villager, int status)
}