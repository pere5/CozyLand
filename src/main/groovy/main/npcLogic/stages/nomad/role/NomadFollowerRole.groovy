package main.npcLogic.stages.nomad.role

import main.npcLogic.Role
import main.npcLogic.Rule
import main.npcLogic.stages.hamlet.rule.HamletVillagerBuilderRule
import main.npcLogic.stages.nomad.NomadTribe
import main.npcLogic.stages.nomad.rule.NomadFollowRule

class NomadFollowerRole extends Role {

    static final String ID = 'nomad_follower'

    NomadFollowerRole(NomadTribe tribe) {
        super(ID, tribe)
    }

    List<Rule> constructRuleList() {

        int rank = Integer.MAX_VALUE
        [
                new NomadFollowRule(--rank),
                //new HomeRule(rank: --rank),
                //new GathererRule(rank: --rank),
                new HamletVillagerBuilderRule(--rank)
        ]
    }
}
