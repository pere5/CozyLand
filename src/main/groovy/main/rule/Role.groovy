package main.rule

import main.villager.Villager

abstract class Role {

    String id
    List<Rule> subjectRules = []
    List<Rule> rules = []
    List<Villager> villagers = []
}
