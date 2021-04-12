package main.things.building.home

import main.Model
import main.model.Villager

class Temple extends Home {

    private static final int MAX_HABITANTS = 1

    Temple(Villager me, int[] tileXY) {
        super(Model.Shape.TEMPLE, MAX_HABITANTS, me, tileXY)
    }
}
