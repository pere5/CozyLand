package main.rule.norole

import main.rule.Role

class NoRole extends Role {

    int id

    NoRole () {

        super.rules << new Affinity()
    }
}
