package main.role.tribe

import main.role.Role
import main.rule.BuilderRule
import main.rule.FollowRule
import main.rule.Rule

class FollowerRole extends Role {

    static final String ID = 'follower'

    static List<Rule> getRules() {
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
