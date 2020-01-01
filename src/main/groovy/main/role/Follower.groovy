package main.role

import main.model.Villager
import main.rule.Rule
import main.rule.shaman.Follow

class Follower extends Role {

    static final String ID = 'follower'

    static List<Rule> followerRules() {
        int rank = Integer.MAX_VALUE
        [
                new Follow(rank: --rank)
        ]
    }

    Follower(Villager boss) {
        super.id = ID
        super.rules = followerRules()
        super.boss = boss
    }

}
