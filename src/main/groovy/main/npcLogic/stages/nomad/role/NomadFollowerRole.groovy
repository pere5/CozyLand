package main.npcLogic.stages.nomad.role

import main.npcLogic.Role
import main.npcLogic.Rule
import main.npcLogic.Tribe
import main.npcLogic.stages.nomad.rule.NomadFollowRule

class NomadFollowerRole extends Role {

    static final String ID = 'nomad_follower'

    NomadFollowerRole(Tribe tribe) {
        super(ID, tribe)
    }

    List<Rule> constructRuleList() {

        int rank = Integer.MAX_VALUE
        [
                new NomadFollowRule(--rank)
        ]
    }
}
