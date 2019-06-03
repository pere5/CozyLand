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
            - [ ] Spara punkterna och kör perSquares med bresenham mellan dem.

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

                def pixelDest = Model.generateXY()
                def tileDestXY = Model.pixelToTileIdx(pixelDest)
                def pixelStart = [villager.x, villager.y] as Double[]
                def pixelStep = pixelStart
                def there = false

                while (!there) {

                    def degree = Model.calculateDegree(pixelStep, pixelDest)
                    def tileStartXY = Model.pixelToTileIdx(pixelStep)

                    def nextSquares = nextSquares(villager, tileStartXY, tileDestXY, degree)

                    if (nextSquares) {
                        def random = Math.random() * 100

                        def nextSquare = nextSquares.find { random >= (it[0][0] as Double) && random <= (it[0][1] as Double) }

                        def newSquare = [tileStartXY[0] + nextSquare[1][0], tileStartXY[1] + nextSquare[1][1]] as int[]

                        def newPixelStep = randomPlaceInSquare(newSquare)

                        villager.actionQueue << new StraightPath(pixelStep, newPixelStep)

                        pixelStep = newPixelStep

                        there = StraightPath.closeEnoughTile(newSquare, tileDestXY)
                        if (there) {
                            villager.actionQueue << new StraightPath(pixelStep, randomPlaceInSquare(tileDestXY), [[[0, 100], tileDestXY]])
                        }
                    } else {
                        there = true
                    }
                }
                villager.toWorkWorker()
            }
        }
    }

    Double[] randomPlaceInSquare(int[] pixelIdx) {
        pixelIdx = Model.tileToPixelIdx(pixelIdx)
        pixelIdx[0] += 1
        pixelIdx[1] += 1
        pixelIdx[0] += (Main.SQUARE_WIDTH - 2) * Math.random()
        pixelIdx[1] += (Main.SQUARE_WIDTH - 2) * Math.random()
        return pixelIdx
    }

    def nextSquares(Villager villager, int[] tileStartXY, int[] tileDestXY, int degree) {

        def (int tileX, int tileY) = tileStartXY

        def nextSquares = []

        def tileNetwork = Model.model.tileNetwork as Tile[][]
        def tile = tileNetwork[tileX][tileY]

        final def squareProbabilities = Model.model.squareProbabilitiesForDegrees[degree]

        squareProbabilities.each { def square ->
            def (int sX, int sY) = square[0]
            def neighborXY = [tileX + sX, tileY + sY] as int[]
            def (int nX, int nY) = neighborXY
            def neighbor = tileNetwork[nX][nY] as Tile
            TravelType travelType = neighbor.travelType
            def squareProbability = square[1] as Double

            if (villager.canTravel(travelType)) {
                if (squareProbability > 0) {
                    if (Model.bresenham(neighborXY, tileDestXY, villager) >= 0) {
                        nextSquares << calculateProbabilityForNeighbor(neighbor, tile, square)
                    }
                }
            }
        }

        if (nextSquares) {
            def globalModifier = 100 / (nextSquares.sum { it[0] } as Double)
            nextSquares.each {
                it[0] *= globalModifier
            }

            Double sum = 0
            nextSquares.each { def square ->
                Double from = sum
                Double to = from + (square[0] as Double)
                sum = to
                square[0] = [from, to]
            }
        }

        return nextSquares
    }

    def calculateProbabilityForNeighbor(Tile neighbor, Tile tile, def square) {
        Double squareProbability = square[1] as Double
        TravelType travelType = neighbor.travelType
        def travelModifierMap = Model.travelModifier as Map<TravelType, Double>
        int heightDifference = neighbor.height - tile.height
        Double travelModifier = travelModifierMap[travelType]
        Double heightModifier = (heightDifference > 0
                ? travelModifierMap[TravelType.UP_HILL]
                : heightDifference == 0
                ? travelModifierMap[TravelType.EVEN]
                : travelModifierMap[TravelType.DOWN_HILL]) as Double
        Double probability = (1 / (heightModifier * travelModifier)) * squareProbability
        [probability, square[0]]
    }
}
