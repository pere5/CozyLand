package main.role.alone

import main.role.Role
import main.rule.AffinityRule
import main.rule.RandomBigWalkRule
import main.rule.Rule

class AloneRole extends Role {

    static final String ID = 'alone'

    static List<Rule> getRules() {
        int rank = Integer.MAX_VALUE
        [
                //new AffinityRule(rank: --rank)
                new RandomBigWalkRule(rank: --rank)
        ]
    }

    AloneRole() {
        super.id = ID
        super.rules = getRules()
    }
}
