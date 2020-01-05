package main.role

import main.model.Villager
import main.rule.Follow
import main.rule.Rule

class Follower extends Role {

    static final String ID = 'follower'

    static List<Rule> getRules() {
        int rank = Integer.MAX_VALUE
        [
                new Follow(rank: --rank)
        ]
    }

    Follower(Villager boss) {
        super.id = ID
        super.rules = getRules()
        super.boss = boss
    }

}
