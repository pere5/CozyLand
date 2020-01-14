package main.role

import main.model.Villager
import main.rule.FollowRule
import main.rule.Rule

class FollowerRole extends Role {

    static final String ID = 'follower'

    static List<Rule> getRules() {
        int rank = Integer.MAX_VALUE
        [
                new FollowRule(rank: --rank)
        ]
    }

    FollowerRole(Villager boss) {
        super.id = ID
        super.rules = getRules()
        super.boss = boss
    }

}
