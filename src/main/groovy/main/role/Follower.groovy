package main.role


import main.model.Villager
import main.thread.RuleWorker

class Follower extends Role {

    static final String ID = 'follower'

    Follower(Villager boss) {
        super.id = ID
        super.rules.addAll(RuleWorker.followerRules())
        super.boss = boss
    }

}
