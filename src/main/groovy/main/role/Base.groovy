package main.role

import main.rule.Affinity
import main.rule.Rule

class Base extends Role {

    static final String ID = 'base'

    static List<Rule> baseRules() {
        int rank = Integer.MAX_VALUE
        [
                new Affinity(rank: --rank)
                //new RandomBigWalk(rank: --rank)
        ]
    }

    Base() {
        super.id = ID
        super.rules = baseRules()
    }
}
