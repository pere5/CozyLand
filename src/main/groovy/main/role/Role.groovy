package main.role

import main.model.Villager
import main.rule.Rule

abstract class Role {

    String id
    List<Rule> subjectRules = []
    List<Rule> rules = []
    List<Villager> villagers = []
}
