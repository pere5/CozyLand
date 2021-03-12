package main

import javaSrc.circulararray.CircularArrayList
import main.model.Path
import main.model.Tile
import main.model.Villager
import main.thread.PathfinderWorker
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

    static Tile w(int x, int y) {
        new Tile(height: 10, size: Main.TILE_WIDTH, x: x, y: y, travelType: Model.TravelType.WATER)
    }

    static Tile p(int x, int y) {
        new Tile(height: 10, size: Main.TILE_WIDTH, x: x, y: y, travelType: Model.TravelType.PLAIN)
    }

    @Test
    void findPath() {
        Model.tileNetwork = [
                [p(0,0), p(0,1), p(0,2)],
                [p(1,0), p(1,1), w(1,2)],
                [p(2,0), w(2,1), w(2,2)]
        ]
        Set<List<Integer>> v = []

        def vi = new Villager()

        def (int[] left0, int[] right0) = new PathfinderWorker().leftRight([2, 2] as int[], [1, 1] as int[], [0, 0] as int[], v, vi)
        def (int[] left1, int[] right1) = new PathfinderWorker().leftRight([2, 1] as int[], [1, 1] as int[], [0, 1] as int[], v, vi)
        def (int[] left2, int[] right2) = new PathfinderWorker().leftRight([2, 0] as int[], [1, 1] as int[], [0, 2] as int[], v, vi)

        assert left0 == [0, 2] as int[] && right0 == [2, 0] as int[]
        assert left1 == [0, 2] as int[] && right1 == [2, 0] as int[]
        assert left2 == null && right2 == [1, 0] as int[]
        //do stuff here
    }
}
