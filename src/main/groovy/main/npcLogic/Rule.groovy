package main.npcLogic

import main.model.Villager

abstract class Rule {

    static int UNREACHABLE = 30
    static int GREAT = 20
    static int GOOD = 10
    static int BAD = 0

    int rank
    abstract int status(Villager villager)
    abstract void planWork(Villager villager, int status)
}