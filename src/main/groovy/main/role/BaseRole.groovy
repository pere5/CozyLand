package main.role

import main.rule.AffinityRule
import main.rule.Rule

class BaseRole extends Role {

    static final String ID = 'base'

    static List<Rule> getRules() {
        int rank = Integer.MAX_VALUE
        [
                new AffinityRule(rank: --rank)
                //new RandomBigWalk(rank: --rank)
        ]
    }

    BaseRole() {
        super.id = ID
        super.rules = getRules()
    }
}
