package main.rule

import main.villager.Villager

abstract class Rule {
    int rank
    abstract int status(Villager villager)
    abstract void startWork(Villager villager, int status)
}