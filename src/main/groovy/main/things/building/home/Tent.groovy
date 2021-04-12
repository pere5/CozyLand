package main.things.building.home

import main.Model
import main.model.Villager

class Tent extends Home {

    private static final int MAX_HABITANTS = 2

    Tent(Villager me, int[] tileXY) {
        super(Model.Shape.TENT, MAX_HABITANTS, me, tileXY)
    }
}
