package main.npcLogic.stages.nomad.role

import main.npcLogic.Role
import main.npcLogic.Rule
import main.npcLogic.Tribe
import main.npcLogic.stages.nomad.rule.NomadShamanRule

class NomadShamanRole extends Role {

    static final String ID = 'nomad_shaman'

    NomadShamanRole(Tribe tribe) {
        super(ID, tribe)
    }

    List<Rule> constructRuleList() {
        int rank = Integer.MAX_VALUE
        [
                new NomadShamanRule(--rank)
        ]
    }
}
