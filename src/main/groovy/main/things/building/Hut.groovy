package main.things.building

import main.Model
import main.model.Tile
import main.model.Villager
import main.utility.ImageUtils

class Hut extends Building {

    private static final int MAX_HABITANTS = 2

    List<Villager> habitants = []

    Hut(Villager me, int[] tileXY) {
        super(Shape.HUT)
        def tileNetwork = Model.tileNetwork as Tile[][]
        def tile = tileNetwork[tileXY[0]][tileXY[1]]
        this.shape = Shape.HUT
        this.image = ImageUtils.shadeImage(Model.shapeProperties[Shape.HUT], tile.color)
        this.x = tileXY[0]
        this.y = tileXY[1]
        habitants << me
    }
}
