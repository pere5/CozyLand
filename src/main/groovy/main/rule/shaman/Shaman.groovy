package main.rule.shaman

import main.rule.Role
import main.thread.RuleWorker

class Shaman extends Role {

    static final String ID = 'shaman'

    Shaman () {
        super.id = ID
        super.rules.addAll(RuleWorker.shamanRules())
    }

}
