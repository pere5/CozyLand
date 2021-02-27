package main.npcLogic.stages.nomad.role

import main.npcLogic.Role
import main.npcLogic.stages.hamlet.rule.BuilderRule
import main.npcLogic.stages.nomad.rule.NomadFollowRule
import main.npcLogic.stages.nomad.NomadTribe
import main.npcLogic.Rule

class NomadFollowerRole extends Role {

    static final String ID = 'follower'

    NomadFollowerRole(NomadTribe tribe) {
        super(ID, tribe)
    }

    List<Rule> constructRuleList() {

        /*
            - Hitta en färdig Hut som ingen bor i
                - Ta den som ditt Home
            - !^ Hitta en oklar Hut har plats för fler byggare
                - Bygg på den
            - !^ Anlägg en ny Hut
                - Bygg på den
         */

        int rank = Integer.MAX_VALUE
        [
                new NomadFollowRule(--rank),
                //new HomeRule(rank: --rank),
                //new GathererRule(rank: --rank),
                new BuilderRule(--rank)
        ]
    }
}
