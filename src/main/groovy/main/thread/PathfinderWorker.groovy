package main.thread


import main.Model
import main.Model.TravelType
import main.Node
import main.villager.StraightPath
import main.villager.Villager

class PathfinderWorker extends Worker {

    /*
        - [ ] optimized random path:
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

                def dest = Model.generateXY()
                def there = false

                while (!there) {
                    def start = [villager.x, villager.y] as Double[]
                    def degree = Model.calculateDegree(start, dest)
                    def nodeXY = Model.pixelToNodeIdx(start)
                    def destXY = Model.pixelToNodeIdx(dest)

                    def nextSquares = nextSquares(villager, nodeXY, destXY, degree)

                    def random = Math.random() * 100

                    def square = nextSquares.find {
                        def from = it[0][0] as Double
                        def to = it[0][1] as Double
                        random >= from && random <= to
                    }

                    square //yeah boi

                    villager.actionQueue << new StraightPath(start, dest, nextSquares)
                    villager.toWorkWorker()

                    there = true
                }
            }
        }
    }

    def nextSquares(Villager villager, int[] nodeXY, int[] destXY, int degree) {

        def (int nodeX, int nodeY) = nodeXY

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
                    if (Model.model.bresenhamMap[neighborXY]) {
                        nextSquares << calculateProbabilityForNeighbor(neighbor, node, square)
                    } else if (hasBresenhamToDest(neighborXY, destXY)) {
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

    boolean hasBresenhamToDest(int[] start, int[] dest) {



        true
    }
}
