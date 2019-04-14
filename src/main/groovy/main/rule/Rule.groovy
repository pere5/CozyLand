package main.rule

import main.villager.Villager

abstract class Rule {
    int rank
    abstract int calculateStatus(Villager villager)
    abstract void initWork(Villager villager, int status)
}