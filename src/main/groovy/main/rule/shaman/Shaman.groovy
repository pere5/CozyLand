package main.rule.shaman

import main.rule.Role

class Shaman extends Role {

    Shaman () {
        super.rules << new Migrate()
    }

}
