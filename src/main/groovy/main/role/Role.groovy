package main.role

import main.rule.Rule

abstract class Role {

    String id
    List<Rule> rules = []
    Tribe tribe
}
