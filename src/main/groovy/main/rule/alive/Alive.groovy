package main.rule.alive

import main.rule.Role
import main.thread.RuleWorker

class Alive extends Role {

    static final String ID = 'alive'

    Alive() {
        super.id = ID
        super.subjectRules.addAll(RuleWorker.aliveRules())
    }
}
