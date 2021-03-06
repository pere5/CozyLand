package main.npcLogic.stages.hamlet.role

import main.npcLogic.Role
import main.npcLogic.Rule
import main.npcLogic.stages.hamlet.rule.HamletChieftainRule
import main.npcLogic.stages.nomad.NomadTribe

class HamletChieftainRole extends Role {

    static final String ID = 'hamlet_chieftain'

    HamletChieftainRole(NomadTribe tribe) {
        super(ID, tribe)
    }

    @Override
    List<Rule> constructRuleList() {
        int rank = Integer.MAX_VALUE
        [
                new HamletChieftainRule(--rank)
        ]
    }
}
