package main.role.tribe

import main.role.Role
import main.rule.BuilderRule
import main.rule.FollowRule
import main.rule.Rule

class FollowerRole extends Role {

    static final String ID = 'follower'

    FollowerRole(NomadTribe tribe) {
        super(tribe, ID)
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
                new FollowRule(rank: --rank),
                //new HomeRule(rank: --rank),
                //new GathererRule(rank: --rank),
                new BuilderRule(rank: --rank)
        ]
    }
}
