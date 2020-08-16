package main.role.tribe

import main.role.Role
import main.rule.BuilderRule
import main.rule.FollowRule
import main.rule.Rule

class FollowerRole extends Role {

    static final String ID = 'follower'

    static List<Rule> getRules() {

        /*
            - Hitta en färdig Hut som ingen bor i
                - Ta den som ditt Home
            - !^ Hitta en oklar Hut har plats för fler byggare
                - Bygg på den
            - !^ Anlägg en ny Hut
                - Bygg på den
         */


        //sätt så att ingen drawable behöver manuellt läggas in i drawables.
        //det skall göras i drawable konstruktorn


        int rank = Integer.MAX_VALUE
        [
                new FollowRule(rank: --rank),
                //new HomeRule(rank: --rank),
                //new GathererRule(rank: --rank),
                new BuilderRule(rank: --rank)
        ]
    }

    FollowerRole(NomadTribe tribe) {
        super.id = ID
        this.rules = getRules()
        this.tribe = tribe
    }
}
