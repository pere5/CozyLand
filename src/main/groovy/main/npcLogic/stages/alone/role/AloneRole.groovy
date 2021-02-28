package main.npcLogic.stages.alone.role


import main.calculator.Utility
import main.npcLogic.Role
import main.npcLogic.Rule
import main.npcLogic.stages.alone.AloneTribe
import main.npcLogic.stages.generic.rule.AffinityRule

class AloneRole extends Role {

    static final String ID = 'alone'

    AloneRole() {
        super(ID, new AloneTribe())
    }

    List<Rule> constructRuleList() {
        int rank = Integer.MAX_VALUE
        [
                new AffinityRule(--rank, Utility.&joinATribe)
                //new RandomBigWalkRule(--rank, Utility.&joinATribe)
        ]
    }
}
