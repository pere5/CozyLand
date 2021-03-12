package main.npcLogic.stages.hamlet.role

import main.npcLogic.Role
import main.npcLogic.Rule
import main.npcLogic.Tribe
import main.npcLogic.stages.hamlet.rule.HamletVillagerBuilderRule
import main.npcLogic.stages.hamlet.rule.HamletVillagerChillRule
import main.npcLogic.stages.hamlet.rule.HamletVillagerGathererRule
import main.npcLogic.stages.hamlet.rule.HamletVillagerHomeRule

class HamletVillagerRole extends Role {

    static final String ID = 'hamlet_villager'

    HamletVillagerRole(Tribe tribe) {
        super(ID, tribe)
    }

    @Override
    List<Rule> constructRuleList() {
        int rank = Integer.MAX_VALUE
        [
                new HamletVillagerHomeRule(--rank),
                new HamletVillagerBuilderRule(--rank),
                new HamletVillagerGathererRule(--rank),
                new HamletVillagerChillRule(--rank)
        ]
    }
}
