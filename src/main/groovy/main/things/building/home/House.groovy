package main.things.building.home

import main.Model
import main.model.Villager

class House extends Home {
    private static final int MAX_HABITANTS = 4

    House(Villager me, int[] tileXY) {
        super(Model.Shape.HUT, MAX_HABITANTS, me, tileXY)
    }
}
