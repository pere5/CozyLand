package main.role


import main.thread.RuleWorker

class Base extends Role {

    static final String ID = 'base'

    Base() {
        super.id = ID
        super.subjectRules.addAll(RuleWorker.baseRules())
    }
}
