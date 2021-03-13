package main.things.building.home


import main.model.Villager

class ShamanLodge extends Home {

    private static final int MAX_HABITANTS = 1

    ShamanLodge(Villager me) {
        super(Shape.HUT, MAX_HABITANTS)
        this.shape = Shape.SHAMAN_LODGE
        def tileXY = me.tileXY
        this.x = tileXY[0]
        this.y = tileXY[1]
        habitants << me
    }
}
