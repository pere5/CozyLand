package main.role

import main.role.tribe.NomadTribe
import main.rule.Rule

abstract class Role {

    String id
    List<Rule> rules = []
    Tribe tribe

    Role(String id) {
        this.id = id
        this.rules = constructRuleList()
    }

    Role(NomadTribe tribe, String id) {
        this.id = id
        this.rules = constructRuleList()
        this.tribe = tribe
    }

    abstract List<Rule> constructRuleList()
}
