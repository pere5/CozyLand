package main.utility

import main.Main
import main.Model
import main.exception.PerIsBorkenException
import main.model.Tile
import main.model.Villager
import main.things.Drawable

import java.awt.geom.Point2D
import java.util.concurrent.ThreadLocalRandom

class Utility {

    static int round(BigDecimal number) {
        if (number > 0) {
            return number + 0.5
        } else {
            return number - 0.5
        }
    }

    static int round(Double number) {
        if (number > 0) {
            return number + 0.5
        } else {
            return number - 0.5
        }
    }

    static int[] round(Double[] numbers) {
        numbers.collect { round(it) }
    }

    static int[] round(List<Double> numbers) {
        numbers.collect { round(it) }
    }

    static Double[] generateXY() {
        Double[] xy = [
                Main.MAP_WIDTH / 2 + generate((Main.MAP_WIDTH / 2.15) as int),
                Main.MAP_HEIGHT / 2 + generate((Main.MAP_HEIGHT / 2.15) as int)
        ]

        def tileXY = pixelToTileIdx(xy)

        def tile = Model.tileNetwork[tileXY[0]][tileXY[1]] as Tile

        if (tile.travelType == Model.TravelType.WATER) {
            return generateXY()
        } else {
            return xy
        }
    }

    static int[] generateTileXY() {
        def tileNetwork = Model.tileNetwork as Tile[][]

        int[] tileXY = [
                tileNetwork.length / 2 + generate((tileNetwork.length / 3) as int),
                tileNetwork[0].length / 2 + generate((tileNetwork[0].length / 3) as int)
        ]

        def tile = Model.tileNetwork[tileXY[0]][tileXY[1]] as Tile

        if (tile.travelType == Model.TravelType.WATER) {
            return generateTileXY()
        } else {
            return tileXY
        }
    }

    static int[] closeRandomTile(Villager me, int[] targetXY, Integer maxTileDist, Integer minTileDist = null) {

        def tileNetwork = Model.tileNetwork as Tile[][]
        def (int tileX, int tileY) = targetXY

        List<int[]> tiles = []

        getTilesWithinRadii(tileX, tileY, maxTileDist) { int x, int y ->
            def tile = tileNetwork[x][y]
            def furtherThanMin = minTileDist ? !withinCircle(tileX, tileY, x, y, minTileDist) : true

            if (furtherThanMin && me.canTravel(tile.travelType)) {
                tiles << tile.tileXY
            }
        }

        if (tiles) {
            return tiles[getRandomIntegerBetween(0, tiles.size() - 1)]
        } else {
            return pixelToTileIdx(me.x, me.y)
        }
    }

    static int[] centroidTile(List<Villager> villagers, Villager me, Integer maxTileDist) {
        def cPixel = [0, 0] as Double[]

        for (Villager villager: villagers) {
            cPixel[0] += villager.x
            cPixel[1] += villager.y
        }

        cPixel[0] = cPixel[0] / villagers.size()
        cPixel[1] = cPixel[1] / villagers.size()

        def cTile = pixelToTileIdx(cPixel)

        def tile = Model.tileNetwork[cTile[0]][cTile[1]] as Tile

        if (me.canTravel(tile.travelType)) {
            return cTile
        } else {
            return closeRandomTile(me, me.tileXY, maxTileDist)
        }
    }

    static int[] antiCentroidTile(List<Villager> villagers, Villager me, Integer maxTileDist) {
        def (int centroidX, int centroidY) = centroidTile(villagers, me, maxTileDist)
        def (int meX, int meY) = me.getTileXY()

        def x = centroidX - meX
        def y = centroidY - meY

        def radians = Math.atan2(y, x)
        def otherDirection = radians + Math.PI

        def degreesTo = Math.toDegrees(radians)
        def degreesFrom = Math.toDegrees(otherDirection)

        def xRatio = Math.cos(otherDirection)
        def yRatio = Math.sin(otherDirection)

        def xDistance = xRatio * maxTileDist
        def yDistance = yRatio * maxTileDist

        def antiCentroidX = (meX + xDistance) as Integer
        def antiCentroidY = (meY + yDistance) as Integer

        if (withinTileNetwork(antiCentroidX, antiCentroidY)) {
            def tile = Model.tileNetwork[antiCentroidX][antiCentroidY]

            if (me.canTravel(tile.travelType)) {
                return [antiCentroidX, antiCentroidY]
            } else {
                return closeRandomTile(me, me.tileXY, maxTileDist)
            }
        } else {
            return closeRandomTile(me, me.tileXY, maxTileDist)
        }
    }

    static boolean closeEnough(Double[] pointA, Double[] pointB) {
        Double xBig = pointA[0] + Main.STEP
        Double xSmall = pointA[0] - Main.STEP
        Double yBig = pointA[1] + Main.STEP
        Double ySmall = pointA[1] - Main.STEP
        return pointB[0] <= xBig && pointB[0] >= xSmall && pointB[1] <= yBig && pointB[1] >= ySmall
    }

    static boolean closeEnoughTile(int[] tileA, int[] tileB) {
        int xBig = tileA[0] + 1
        int xSmall = tileA[0] - 1
        int yBig = tileA[1] + 1
        int ySmall = tileA[1] - 1
        return tileB[0] <= xBig && tileB[0] >= xSmall && tileB[1] <= yBig && tileB[1] >= ySmall
    }

    static List<Double[]> randomPlacesInTileList(List<int[]> tiles) {
        tiles.collect { def tile ->
            randomPlaceInTile(tile)
        }
    }

    static Double[] randomPlaceInTile(int[] tile) {
        if (Model.tileNetwork[tile[0]][tile[1]].travelType == Model.TravelType.WATER) {
            throw new PerIsBorkenException()
        }
        def pixelIdx = tileToPixelIdx(tile)
        pixelIdx[0] += (Main.TILE_WIDTH - 2) * Math.random() + 1
        pixelIdx[1] += (Main.TILE_WIDTH - 2) * Math.random() + 1
        return pixelIdx
    }

    static int tileRange(Drawable a, Drawable b) {
        pixelToTileIdx(pixelRange(a, b))
    }

    static Double pixelRange(Drawable a, Drawable b) {
        return a.id != b.id ? (Math.sqrt(Math.pow((a.x - b.x), 2) + Math.pow((a.y - b.y), 2))) : java.lang.Double.MAX_VALUE
    }

    static Double generate(int distance) {
        return distance - ThreadLocalRandom.current().nextInt(0, distance * 2 + 1)
    }

    static int pixelToTileIdx(Double pixel) {
        pixel / Main.TILE_WIDTH
    }

    static int[] pixelToTileIdx(List<Double> pixels) {
        pixels.collect { it / Main.TILE_WIDTH }
    }

    static int[] pixelToTileIdx(Double a, Double b) {
        [ a / Main.TILE_WIDTH, b / Main.TILE_WIDTH ] as int[]
    }

    static int[] pixelToTileIdx(Double[] pixels) {
        pixels.collect { it / Main.TILE_WIDTH }
    }

    static Double tileToPixelIdx(int tile) {
        tile * Main.TILE_WIDTH
    }

    static Double[] tileToPixelIdx(int[] tile) {
        tile.collect { (it * Main.TILE_WIDTH) }
    }

    static int[] tileToPixelIdx(List<Integer> tile) {
        tile.collect { (it * Main.TILE_WIDTH) }
    }

    static int calculateDegreeRound(int[] start, int[] dest) {
        Double deg = Math.toDegrees(Math.atan2(dest[1] - start[1], dest[0] - start[0]))
        round(deg >= 0 ? deg : deg + 360)
    }

    static int distance(int[] a, int[] b) {
        Point2D.distance(a[0], a[1], b[0], b[1])
    }

    //https://stackoverflow.com/questions/40779343/java-loop-through-all-pixels-in-a-2d-circle-with-center-x-y-and-radius?noredirect=1&lq=1
    static void getTilesWithinRadii(int tX, int tY, int r, Closure function) {
        // iterate through all y-coordinates
        for (int y = tY - r; y <= tY + r; y++) {
            // iterate through all x-coordinates
            for (int x = tX - r; x <= tX + r; x++) {
                if (withinCircle(x, y, tX, tY, r)) {
                    if (withinTileNetwork(x, y)) {
                        //TestPrints.printRadii(x, y, me)
                        function(x, y)
                    }
                }
            }
        }
    }

    static boolean withinCircle(int[] xy, int[] dxy, int r) {
        //(x - center_x)^2 + (y - center_y)^2 < radius^2
        withinCircle(xy[0], xy[1], dxy[0], dxy[1], r)
    }

    static boolean withinCircle(int x, int y, int tX, int tY, int r) {
        //(x - center_x)^2 + (y - center_y)^2 < radius^2
        Math.pow(x - tX, 2) + Math.pow(y - tY, 2) <= Math.pow(r, 2)
    }

    static boolean withinTileNetwork(int x, int y) {
        x >= 0 && x <= Model.tileNetwork.length - 1 && y >= 0 && y <= Model.tileNetwork[0].length - 1
    }

    static Integer getRandomIntegerBetween(Integer min, Integer max) {
        return (min + (Math.random() * ((max + 1) - min))).toInteger()
    }

    static boolean compareTiles(int[] a, int[] b) {
        a[0] == b[0] && a[1] == b[1]
    }

    static void placeInTileNetwork(Villager villager) {

        def (int tileX, int tileY) = villager.getTileXY()
        def correctTile = Model.tileNetwork[tileX][tileY] as Tile

        if (!villager.tile) {
            villager.tile = correctTile
            villager.tile.villagers << villager
        } else if (correctTile.id != villager.tile.id) {
            villager.tile.villagers.remove(villager)
            villager.tile = correctTile
            villager.tile.villagers << villager
        }

        def test = false
        if (test) {
            def tileNetwork = Model.tileNetwork
            def matches = []
            for (int x = 0; x < tileNetwork.length; x++) {
                for (int y = 0; y < tileNetwork[x].length; y++) {
                    Tile tile = tileNetwork[x][y]
                    if (villager.id in tile.villagers.id) {
                        matches << [tile: tile, villager: villager]
                    }
                }
            }

            def one = correctTile.id == villager.tile.id
            def two = villager.id == villager.tile.villagers.find { it.id == villager.id }?.id
            def three = matches.size() == 1
            def four = villager.id in (matches.find().tile as Tile).villagers.id

            if (!(one && two && three && four)) {
                throw new PerIsBorkenException()
            }
        }
    }
}
