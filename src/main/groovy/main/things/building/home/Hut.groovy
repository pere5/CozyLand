package main.things.building.home

import main.Model
import main.model.Villager

class Hut extends Home {

    private static final int MAX_HABITANTS = 2

    Hut(Villager me) {
        super(Model.Shape.HUT, MAX_HABITANTS, me)
    }
}
