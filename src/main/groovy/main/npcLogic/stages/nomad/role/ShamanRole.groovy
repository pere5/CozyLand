package main.npcLogic.stages.nomad.role


import main.npcLogic.Role
import main.npcLogic.stages.nomad.NomadTribe
import main.npcLogic.stages.hamlet.rule.ShamanBuildRule
import main.npcLogic.stages.nomad.rule.ShamanNomadRule
import main.npcLogic.Rule

class ShamanRole extends Role {

    static final String ID = 'shaman'

    ShamanRole(NomadTribe tribe) {
        super(ID, tribe)
    }

    List<Rule> constructRuleList() {
        int rank = Integer.MAX_VALUE
        [
                new ShamanNomadRule(rank: --rank),
                new ShamanBuildRule(rank: --rank)
        ]
    }
}
