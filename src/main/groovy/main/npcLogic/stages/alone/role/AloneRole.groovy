package main.npcLogic.stages.alone.role

import main.npcLogic.Role
import main.npcLogic.Rule
import main.npcLogic.Tribe
import main.npcLogic.stages.alone.rule.AffinityRule

class AloneRole extends Role {

    static final String ID = 'alone'

    AloneRole(Tribe tribe) {
        super(ID, tribe)
    }

    List<Rule> constructRuleList() {
        int rank = Integer.MAX_VALUE
        [
                new AffinityRule(--rank)
                //new RandomBigWalkRule(--rank)
        ]
    }
}
