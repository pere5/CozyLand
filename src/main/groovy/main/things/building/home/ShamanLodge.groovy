package main.things.building.home

import main.Model
import main.model.Villager
import main.utility.Utility

class ShamanLodge extends Home {

    private static final int MAX_HABITANTS = 1

    ShamanLodge(Villager me) {
        super(Model.Shape.SCROLL, MAX_HABITANTS)
        def pixels = Utility.randomPlaceInTile(me.tileXY)
        this.x = pixels[0]
        this.y = pixels[1]
        habitants << me
        me.home = this
        this.shape = Model.Shape.SCROLL
    }
}
