package main

import javaSrc.circulararray.CircularArrayList
import main.calculator.Path
import main.model.Tile
import main.thread.PathfinderWorker
import main.villager.Villager
import org.junit.Test

class Tests2 {

    @Test
    void fun() {
        def sw = Main.TILE_WIDTH
        def w = Model.TravelType.WATER
        def p = Model.TravelType.PLAIN

        Model.tileNetwork = [
                [new Tile(height: 10, size: sw, x: 0, y: 0, travelType: p), new Tile(height: 10, size: sw, x: 0, y: 1, travelType: w)]
        ]

        def idx = Path.bresenham([0, 0] as int[], [0, 1] as int[], new Villager())

        assert idx != 0
    }

    @Test
    void circularArray() {
        def l = [1, 2, 3] as CircularArrayList
        assert (-10..10).collect {l.get(it)} == [3,1,2,3,1,2,3,1,2,3, 1,2,3,1,2,3,1,2,3,1,2]
    }

    @Test
    void findPath() {
        Model.tileNetwork = [
                [Tile.p(0,0), Tile.p(0,1), Tile.p(0,2), Tile.p(0,3)],
                [Tile.p(1,0), Tile.p(1,1), Tile.w(1,2), Tile.w(1,3)],
                [Tile.p(2,0), Tile.w(2,1), Tile.w(2,2), Tile.w(2,3)],
                [Tile.p(3,0), Tile.w(3,1), Tile.w(3,2), Tile.w(3,3)]
        ]
        def (int[] left, int[] right) = new PathfinderWorker().findPath([2,2] as int[], [1,1] as int[], [0,0] as int[], [] as Set<List<Integer>>, new Villager())

        assert left == [0, 2] as int[]
        assert right == [2, 0] as int[]
        //do stuff here
    }
}
