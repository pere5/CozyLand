package main.npcLogic.stages.alone.role

import main.Main
import main.Model
import main.model.Villager
import main.npcLogic.Role
import main.npcLogic.stages.generic.rule.AffinityRule
import main.npcLogic.stages.alone.AloneTribe
import main.npcLogic.Rule
import main.npcLogic.stages.generic.rule.RandomBigWalkRule
import main.npcLogic.stages.nomad.NomadTribe
import main.npcLogic.stages.nomad.role.NomadFollowerRole
import main.npcLogic.stages.nomad.role.NomadShamanRole

import java.awt.Color

class AloneRole extends Role {

    static final String ID = 'alone'

    AloneRole() {
        super(ID, new AloneTribe())
    }

    List<Rule> constructRuleList() {
        int rank = Integer.MAX_VALUE
        [
                new AffinityRule(--rank, Model.&joinATribe)
                //new RandomBigWalkRule(--rank, Model.&joinATribe)
        ]
    }
}
