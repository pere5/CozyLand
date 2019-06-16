package main.thread

import main.Main
import main.Model
import main.Model.TravelType
import main.Tile
import main.villager.StraightPath
import main.villager.Villager

class PathfinderWorker extends Worker {

    /*
        - [ ] Bresenham binary search per star algorithm, "optimized random path":
            - [ ] Kör en perStar mot målet
            - [ ] Spara perStar i en buffrad array
            - [ ] Binärsök i buffern så långa bresenham steg som möjligt
            - [ ] Spara punkterna och kör perTiles med bresenham mellan dem.

        - [ ] kör steg nod för nod
        - [ ] för nästa steg:
          - [x] kör 90 grader med mittersta graden pekandes mot målet
          - [x] fördela ut graderna lika till grann tilesen
          - [x] hårdkoda det med en färdig lösning per grad för alla 360 grader.
          - [x] lägg upp en (normal) fördelning av sannolikhet för graderna över 90 grader.
          - [x] hårdkoda fördelningen för 0 -> 90 grader med max i 45
          - [x] beräkna genomsnittliga sannolikheten för varje grann nod relativt till de andra tiles utifrån gradernas sannolikheter
          - [x] omfördela sannolikheterna mot vaje nod baserat på tilens movementCost relativt till de andra tilsen
     */

    def update() {

        for (Villager villager: Model.model.villagers) {
            if (villager.pathfinderWorker) {

                /*
                    if (villager.pathfinderWorker) {

                        def pixelDest = Model.generateXY()
                        def pixelStart = [villager.x, villager.y] as Double[]


                        def idx = perStarToGoal()
                        def tiles = longestPossibleBresenhams(idx)

                        def pixels = ([villager.x, villager.y] + tiles.collect {
                            randomPlaceInTile(tileDestXY)
                        }) as int[][]

                        tiles.each { int[] tile ->
                            perTilesWithBresenham(tiles[i][0], tiles[i][1], villager)
                        }

                        villager.toWorkWorker()
                    }
                 */


                def pixelDest = Model.generateXY()
                def tileDestXY = Model.pixelToTileIdx(pixelDest)
                def pixelStart = [villager.x, villager.y] as Double[]
                def pixelStep = pixelStart
                def there = false

                while (!there) {

                    def degree = Model.calculateDegreeRound(pixelStep, pixelDest)
                    def tileStartXY = Model.pixelToTileIdx(pixelStep)

                    def nextTiles = nextTiles(villager, tileStartXY, tileDestXY, degree)

                    if (nextTiles) {
                        def random = Math.random() * 100

                        def nextTile = nextTiles.find { random >= (it[0][0] as Double) && random <= (it[0][1] as Double) }

                        def newTile = [tileStartXY[0] + nextTile[1][0], tileStartXY[1] + nextTile[1][1]] as int[]

                        def newPixelStep = randomPlaceInTile(newTile)

                        villager.actionQueue << new StraightPath(pixelStep, newPixelStep)

                        pixelStep = newPixelStep

                        there = StraightPath.closeEnoughTile(newTile, tileDestXY)
                        if (there) {
                            villager.actionQueue << new StraightPath(pixelStep, randomPlaceInTile(tileDestXY), [[[0, 100], tileDestXY]])
                        }
                    } else {
                        there = true
                    }
                }
                villager.toWorkWorker()
            }
        }
    }

    int[][] longestPossibleBresenhams(int i) {
        null
    }

    int perStarToGoal() {
        0
    }

    private void perTilesWithBresenham(Double[] pixelDest, Double[] pixelStart, Villager villager) {
        def tileDestXY = Model.pixelToTileIdx(pixelDest)
        def pixelStep = pixelStart
        def there = false

        while (!there) {

            def degree = Model.calculateDegreeRound(pixelStep, pixelDest)
            def tileStartXY = Model.pixelToTileIdx(pixelStep)

            def nextTiles = nextTiles(villager, tileStartXY, tileDestXY, degree)

            if (nextTiles) {
                def random = Math.random() * 100

                def nextTile = nextTiles.find { random >= (it[0][0] as Double) && random <= (it[0][1] as Double) }

                def newTile = [tileStartXY[0] + nextTile[1][0], tileStartXY[1] + nextTile[1][1]] as int[]

                def newPixelStep = randomPlaceInTile(newTile)

                villager.actionQueue << new StraightPath(pixelStep, newPixelStep)

                pixelStep = newPixelStep

                there = StraightPath.closeEnoughTile(newTile, tileDestXY)
                if (there) {
                    villager.actionQueue << new StraightPath(pixelStep, randomPlaceInTile(tileDestXY), [[[0, 100], tileDestXY]])
                }
            } else {
                there = true
            }
        }
    }

    Double[] randomPlaceInTile(int[] pixelIdx) {
        pixelIdx = Model.tileToPixelIdx(pixelIdx)
        pixelIdx[0] += 1
        pixelIdx[1] += 1
        pixelIdx[0] += (Main.TILE_WIDTH - 2) * Math.random()
        pixelIdx[1] += (Main.TILE_WIDTH - 2) * Math.random()
        return pixelIdx
    }

    def nextTiles(Villager villager, int[] tileStartXY, int[] tileDestXY, int degree) {

        def (int tileX, int tileY) = tileStartXY

        def nextTiles = []

        def tileNetwork = Model.model.tileNetwork as Tile[][]
        def tile = tileNetwork[tileX][tileY]

        final def tileProbabilities = Model.model.tileProbabilitiesForDegrees[degree]

        tileProbabilities.each { def neighborTile ->
            def (int sX, int sY) = neighborTile[0]
            def neighborXY = [tileX + sX, tileY + sY] as int[]
            def (int nX, int nY) = neighborXY
            if (nX >= 0 && nY >= 0 && nX < tileNetwork.length && nY < tileNetwork[0].length) {
                def neighbor = tileNetwork[nX][nY] as Tile
                TravelType travelType = neighbor.travelType
                def tileProbability = neighborTile[1] as Double

                if (villager.canTravel(travelType)) {
                    if (tileProbability > 0) {
                        if (Model.bresenham(neighborXY, tileDestXY, villager) >= 0) {
                            nextTiles << calculateProbabilityForNeighbor(neighbor, tile, neighborTile)
                        }
                    }
                }
            }
        }

        def globalModifier = 100 / ((nextTiles.sum { it[0] } ?: 1) as Double)
        nextTiles.each {
            it[0] *= globalModifier
        }

        Double sum = 0
        nextTiles.each { def neighborTile ->
            Double from = sum
            Double to = from + (neighborTile[0] as Double)
            sum = to
            neighborTile[0] = [from, to]
        }

        return nextTiles
    }

    def calculateProbabilityForNeighbor(Tile neighbor, Tile tile, def neighborTile) {
        Double tileProbability = neighborTile[1] as Double
        TravelType travelType = neighbor.travelType
        def travelModifierMap = Model.travelModifier as Map<TravelType, Double>
        int heightDifference = neighbor.height - tile.height
        Double travelModifier = travelModifierMap[travelType]
        Double heightModifier = (heightDifference > 0
                ? travelModifierMap[TravelType.UP_HILL]
                : heightDifference == 0
                ? travelModifierMap[TravelType.EVEN]
                : travelModifierMap[TravelType.DOWN_HILL]) as Double
        Double probability = (1 / (heightModifier * travelModifier)) * tileProbability
        [probability, neighborTile[0]]
    }
}
