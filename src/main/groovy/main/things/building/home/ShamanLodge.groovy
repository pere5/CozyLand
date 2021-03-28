package main.things.building.home


import main.model.Villager
import main.utility.Utility

class ShamanLodge extends Home {

    private static final int MAX_HABITANTS = 1

    ShamanLodge(Villager me) {
        super(Shape.SCROLL, MAX_HABITANTS)
        def pixels = Utility.randomPlaceInTile(me.tileXY)
        this.x = pixels[0]
        this.y = pixels[1]
        habitants << me
        me.home = this
        this.shape = Shape.SCROLL
    }
}
