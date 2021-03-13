package main.things.building.home


import main.model.Villager

class Hut extends Home {

    private static final int MAX_HABITANTS = 2

    Hut(Villager me) {
        super(Shape.HUT, MAX_HABITANTS)
        this.shape = Shape.HUT
        def tileXY = me.tileXY
        this.x = tileXY[0]
        this.y = tileXY[1]
        habitants << me
        me.home = this
    }
}
