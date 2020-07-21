package main.role.tribe


import main.role.Role
import main.rule.Rule
import main.rule.ShamanBuildVillageRule
import main.rule.ShamanWalkRule

class ShamanRole extends Role {

    static final String ID = 'shaman'

    static List<Rule> getRules() {
        int rank = Integer.MAX_VALUE
        [
                new ShamanWalkRule(rank: --rank),
                //new ShamanBuildVillageRule(rank: --rank)
        ]
    }

    ShamanRole(NomadTribe tribe) {
        super.id = ID
        this.rules = getRules()
        this.tribe = tribe
    }
}
