package main.npcLogic.stages.nomad.role

import main.npcLogic.Role
import main.npcLogic.Rule
import main.npcLogic.stages.hamlet.rule.HamletChieftainRule
import main.npcLogic.stages.nomad.NomadTribe
import main.npcLogic.stages.nomad.rule.NomadShamanRule

class NomadShamanRole extends Role {

    static final String ID = 'nomad_shaman'

    NomadShamanRole(NomadTribe tribe) {
        super(ID, tribe)
    }

    List<Rule> constructRuleList() {
        int rank = Integer.MAX_VALUE
        [
                new NomadShamanRule(--rank),
                new HamletChieftainRule(--rank)
        ]
    }
}
