package main.npcLogic.stages.hamlet.role

import main.npcLogic.Role
import main.npcLogic.Rule
import main.npcLogic.Tribe
import main.npcLogic.stages.hamlet.rule.HamletVillagerBuilderRule

class HamletVillagerRole extends Role {

    static final String ID = 'hamlet_villager'

    HamletVillagerRole(Tribe tribe) {
        super(ID, tribe)
    }

    /*
        - Hitta en färdig Hut som ingen bor i
            - Ta den som ditt Home
        - !^ Hitta en oklar Hut har plats för fler byggare
            - Bygg på den
        - !^ Anlägg en ny Hut
            - Bygg på den
     */

    @Override
    List<Rule> constructRuleList() {
        int rank = Integer.MAX_VALUE
        [
                //new HamletVillagerHomeRule(--rank),
                //new HamletVillagerGathererRule(--rank),
                new HamletVillagerBuilderRule(--rank)
        ]
    }
}
