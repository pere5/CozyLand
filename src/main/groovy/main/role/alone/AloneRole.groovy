package main.role.alone

import main.role.Role
import main.role.tribe.NomadTribe
import main.rule.AffinityRule
import main.rule.RandomBigWalkRule
import main.rule.Rule

class AloneRole extends Role {

    static final String ID = 'alone'

    AloneRole() {
        super(ID)
    }

    List<Rule> constructRuleList() {
        int rank = Integer.MAX_VALUE
        [
                //new AffinityRule(rank: --rank)
                new RandomBigWalkRule(rank: --rank)
        ]
    }
}
