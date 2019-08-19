package main.rule

import main.model.Villager

abstract class Role {

    String id
    List<Rule> subjectRules = []
    List<Rule> rules = []
    List<Villager> villagers = []
}
