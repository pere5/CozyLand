package main.things.building.home

import main.Model
import main.model.Villager

class ShamanLodge extends Home {

    private static final int MAX_HABITANTS = 1

    ShamanLodge(Villager me) {
        super(Model.Shape.TEMPLE, MAX_HABITANTS, me)
    }
}
