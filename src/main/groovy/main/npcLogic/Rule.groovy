package main.npcLogic

import main.Model
import main.model.Villager

abstract class Rule {

    static int UNREACHABLE = 30
    static int GREAT = 20
    static int GOOD = 10
    static int BAD = 0

    int id
    int rank

    Rule () {
        this.id = Model.getNewId()
    }

    abstract int status(Villager villager)
    abstract void planWork(Villager villager, int status)
}