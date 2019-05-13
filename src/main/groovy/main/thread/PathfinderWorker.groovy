package main.thread

import main.Main
import main.Model
import main.Model.TravelType
import main.Node
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
          - [x] fördela ut graderna lika till grann noderna
          - [x] hårdkoda det med en färdig lösning per grad för alla 360 grader.
          - [x] lägg upp en (normal) fördelning av sannolikhet för graderna över 90 grader.
          - [x] hårdkoda fördelningen för 0 -> 90 grader med max i 45
          - [x] beräkna genomsnittliga sannolikheten för varje grann nod relativt till de andra noderna utifrån gradernas sannolikheter
          - [x] omfördela sannolikheterna mot vaje nod baserat på nodens movementCost relativt till de andra noderna
     */

    def update() {

        for (Villager villager: Model.model.villagers) {
            if (villager.pathfinderWorker) {

                def pixelDest = Model.generateXY()
                def nodeDestXY = Model.pixelToNodeIdx(pixelDest)
                def pixelStart = [villager.x, villager.y] as Double[]
                def pixelStep = pixelStart
                def there = false

                while (!there) {

                    def degree = Model.calculateDegree(pixelStep, pixelDest)
                    def nodeStartXY = Model.pixelToNodeIdx(pixelStep)

                    def nextSquares = nextSquares(villager, nodeStartXY, nodeDestXY, degree)

                    if (nextSquares) {
                        def random = Math.random() * 100

                        def nextSquare = nextSquares.find { random >= (it[0][0] as Double) && random <= (it[0][1] as Double) }

                        def newSquare = [nodeStartXY[0] + nextSquare[1][0], nodeStartXY[1] + nextSquare[1][1]] as int[]

                        def newPixelStep = randomPlaceInSquare(newSquare)

                        villager.actionQueue << new StraightPath(pixelStep, newPixelStep, nextSquares)

                        pixelStep = newPixelStep

                        int xBig = nodeDestXY[0] + 1
                        int xSmall = nodeDestXY[0] - 1
                        int yBig = nodeDestXY[1] + 1
                        int ySmall = nodeDestXY[1] - 1
                        there = newSquare[0] <= xBig && newSquare[0] >= xSmall && newSquare[1] <= yBig && newSquare[1] >= ySmall
                    } else {
                        there = true
                    }
                }
                villager.toWorkWorker()
            }
        }
    }

    Double[] randomPlaceInSquare(int[] pixelIdx) {
        pixelIdx = Model.nodeToPixelIdx(pixelIdx)
        pixelIdx[0] += (Main.SQUARE_WIDTH * Math.random())
        pixelIdx[1] += (Main.SQUARE_WIDTH * Math.random())
        return pixelIdx
    }

    def nextSquares(Villager villager, int[] nodeStartXY, int[] nodeDestXY, int degree) {

        def (int nodeX, int nodeY) = nodeStartXY

        def nextSquares = []

        def nodeNetwork = Model.model.nodeNetwork as Node[][]
        def node = nodeNetwork[nodeX][nodeY]

        final def squareProbabilities = Model.model.squareProbabilitiesForDegrees[degree]

        squareProbabilities.each { def square ->
            def (int sX, int sY) = square[0]
            def neighborXY = [nodeX + sX, nodeY + sY] as int[]
            def (int nX, int nY) = neighborXY
            def neighbor = nodeNetwork[nX][nY] as Node
            TravelType travelType = neighbor.travelType
            def squareProbability = square[1] as Double

            if (villager.canTravel(travelType)) {
                if (squareProbability > 0) {
                    if (Model.bresenham(neighborXY, nodeDestXY, villager) >= 0) {
                        nextSquares << calculateProbabilityForNeighbor(neighbor, node, square)
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

    def calculateProbabilityForNeighbor(Node neighbor, Node node, def square) {
        Double squareProbability = square[1] as Double
        TravelType travelType = neighbor.travelType
        def travelModifierMap = Model.travelModifier as Map<TravelType, Double>
        int heightDifference = neighbor.height - node.height
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
