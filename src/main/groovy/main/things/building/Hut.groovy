package main.things.building

import main.Model
import main.model.Tile
import main.model.Villager

class Hut extends Building {

    private static final int MAX_HABITANTS = 2

    List<Villager> habitants = []

    Hut(Villager me, int[] tileXY) {
        super()
        def tileNetwork = Model.tileNetwork as Tile[][]
        def tile = tileNetwork[tileXY[0]][tileXY[1]]
        this.shape = shape
        this.image = Model.shadeImage(Model.shapeProperties[Shape.HUT].image, tile.color)
        this.x = tileXY[0]
        this.y = tileXY[1]
        habitants << me
    }
}
