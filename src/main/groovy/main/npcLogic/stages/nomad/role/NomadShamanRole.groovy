package main.npcLogic.stages.nomad.role


import main.npcLogic.Role
import main.npcLogic.stages.hamlet.rule.ShamanBuildRule
import main.npcLogic.stages.nomad.NomadTribe
import main.npcLogic.stages.nomad.rule.NomadShamanRule
import main.npcLogic.Rule

class NomadShamanRole extends Role {

    static final String ID = 'shaman'

    NomadShamanRole(NomadTribe tribe) {
        super(ID, tribe)
    }

    List<Rule> constructRuleList() {
        int rank = Integer.MAX_VALUE
        [
                new NomadShamanRule(--rank),
                new ShamanBuildRule(--rank)
        ]
    }
}
