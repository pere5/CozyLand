package main.role


import main.thread.RuleWorker

class Base extends Role {

    static final String ID = 'alive'

    Base() {
        super.id = ID
        super.subjectRules.addAll(RuleWorker.aliveRules())
    }
}
