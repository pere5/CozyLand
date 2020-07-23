package main.role.tribe


import main.role.Role
import main.rule.Rule
import main.rule.ShamanBuildRule
import main.rule.ShamanNomadRule

class ShamanRole extends Role {

    static final String ID = 'shaman'

    static List<Rule> getRules() {
        int rank = Integer.MAX_VALUE
        [
                new ShamanNomadRule(rank: --rank),
                new ShamanBuildRule(rank: --rank)
        ]
    }

    ShamanRole(NomadTribe tribe) {
        super.id = ID
        this.rules = getRules()
        this.tribe = tribe
    }
}
