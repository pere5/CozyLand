package main.rule.shaman

import main.rule.Role

class Shaman extends Role {

    static final String ID = 'shaman'

    Shaman () {
        super.id = ID
        super.rules << new Migrate()
    }

}
