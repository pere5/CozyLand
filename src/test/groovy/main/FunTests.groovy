package main

import main.model.Tile
import main.villager.Villager
import org.junit.Test

class FunTests {

    @Test
    void fun() {
        def sw = Main.TILE_WIDTH
        def w = Model.TravelType.WATER
        def p = Model.TravelType.PLAIN

        Model.model.tileNetwork = [
                [new Tile(height: 10, size: sw, x: 0, y: 0, travelType: p), new Tile(height: 10, size: sw, x: 0, y: 1, travelType: w)]
        ]

        def idx = Model.bresenham([0,0] as int[], [0,1] as int[], new Villager())

        assert idx != 0
    }
}
