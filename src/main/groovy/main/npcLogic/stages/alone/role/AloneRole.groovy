package main.npcLogic.stages.alone.role

import main.npcLogic.Role
import main.npcLogic.stages.alone.rule.AffinityRule
import main.npcLogic.stages.alone.AloneTribe
import main.npcLogic.Rule

class AloneRole extends Role {

    static final String ID = 'alone'

    AloneRole() {
        super(ID, new AloneTribe())
    }

    List<Rule> constructRuleList() {
        int rank = Integer.MAX_VALUE
        [
                new AffinityRule(rank: --rank)
                //new RandomBigWalkRule(rank: --rank)
        ]
    }
}
