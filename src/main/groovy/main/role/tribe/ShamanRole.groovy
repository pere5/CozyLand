package main.role.tribe


import main.role.Role
import main.rule.Rule
import main.rule.ShamanBuildRule
import main.rule.ShamanNomadRule

class ShamanRole extends Role {

    static final String ID = 'shaman'

    ShamanRole(NomadTribe tribe) {
        super(tribe, ID)
    }

    List<Rule> constructRuleList() {
        int rank = Integer.MAX_VALUE
        [
                new ShamanNomadRule(rank: --rank),
                new ShamanBuildRule(rank: --rank)
        ]
    }
}
