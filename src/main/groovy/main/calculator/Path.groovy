package main.calculator

import main.Main
import main.Model
import main.model.Tile
import main.villager.Villager

class Path {

    static int[][] bresenhamBuffer = new int[Main.WINDOW_WIDTH + Main.WINDOW_HEIGHT][2]

    static int bresenham(int[] tileStart, int[] tileDest, Villager villager = null) {
        def (int x1, int y1) = tileStart
        def (int x2, int y2) = tileDest

        // delta of exact value and rounded value of the dependent variable
        int d = 0

        int dx = Math.abs(x2 - x1)
        int dy = Math.abs(y2 - y1)

        int dx2 = 2 * dx // slope scaling factors to
        int dy2 = 2 * dy // avoid floating point

        int ix = x1 < x2 ? 1 : -1 // increment direction
        int iy = y1 < y2 ? 1 : -1

        int x = x1
        int y = y1

        int idx = 0

        def tileNetwork = Model.tileNetwork as Tile[][]

        while (true) {

            bresenhamBuffer[idx][0] = x
            bresenhamBuffer[idx][1] = y

            if ((villager && !villager.canTravel(tileNetwork[x][y].travelType))) {
                return idx
            }

            if (dx >= dy) {
                if (x == x2) {
                    return idx
                }
                x += ix
                d += dy2
                if (d > dx) {
                    y += iy
                    d -= dx2
                }
            } else {
                if (y == y2) {
                    return idx
                }
                y += iy
                d += dx2
                if (d > dy) {
                    x += ix
                    d -= dy2
                }
            }
            idx++
        }
    }
}
