package main.rule.alive

import main.rule.Role

class Alive extends Role {

    static final String ID = 'alive'

    Alive() {
        super.id = ID
        super.rules << new Affinity()
    }
}
