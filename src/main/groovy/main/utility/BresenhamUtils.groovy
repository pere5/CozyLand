package main.utility


import main.Model
import main.model.Tile
import main.model.Villager

class BresenhamUtils {

    static int bresenham(int[] tileStart, int[] tileDest, int[][] buffer, Villager villager = null) {
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

            buffer[idx][0] = x
            buffer[idx][1] = y

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

    static int[] farthestTileWithBresenham(Villager me, int[] tileDest, List<Model.TravelType> avoidList, int[][] bresenhamBuffer) {

        def tileXY = me.tileXY

        if (Model.tileNetwork[tileDest[0]][tileDest[1]].travelType in avoidList) {
            def idx = bresenham(tileXY, tileDest, bresenhamBuffer, me)
            for (int i = idx; i >= 0; i--) {
                def (int x, int y) = bresenhamBuffer[i].clone()
                def travelType = Model.tileNetwork[x][y].travelType
                if (me.canTravel(travelType) && !(travelType in avoidList)) {
                    return [x, y] as int[]
                }
            }
            return tileXY
        } else {
            return tileDest
        }
    }
}
