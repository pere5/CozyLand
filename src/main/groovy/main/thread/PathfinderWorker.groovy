package main.thread


import main.Model
import main.Model.TravelType
import main.Node
import main.villager.StraightPath
import main.villager.Villager

class PathfinderWorker extends Worker {

    /*
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

                def visitedSquares = [:]
                def exhaustedSquares = [:]

                def there = false

                while (!there) {
                    def start = [villager.x, villager.y] as Double[]
                    def dest = Model.generateXY()
                    def degree = Model.calculateDegree(start, dest)
                    def nodeXY = Model.pixelToNodeIdx(start)

                    visitedSquares[nodeXY] = Boolean.TRUE

                    def nextSquares = nextSquares(villager, nodeXY, degree, visitedSquares)

                    def square
                    if (nextSquares) {
                        def random = Math.random() * 100

                        square = nextSquares.find {
                            def from = it[0][0] as Double
                            def to = it[0][1] as Double
                            random >= from && random <= to
                        }
                    } else {
                        exhaustedSquares[nodeXY] = Boolean.TRUE
                        square = unExhaustedSquareClosestToDest(nodeXY, degree, exhaustedSquares, villager)
                    }

                    square //yeah boi

                    villager.actionQueue << new StraightPath(start, dest, nextSquares)
                    villager.toWorkWorker()

                    there = true
                }
            }
        }
    }

    def unExhaustedSquareClosestToDest(int[] nodeXY, int degree, def exhaustedSquares, Villager villager) {

        def (int nodeX, int nodeY) = nodeXY

        def candidates = []

        Model.squareDegrees.each { def square ->
            def (int sX, int sY) = square
            def nX = nodeX + sX
            def nY = nodeY + sY
            def neighbor = nodeNetwork[nX][nY] as Node
            TravelType travelType = neighbor.travelType

            if (villager.canTravel(travelType)) {





                candidates << [1d, square[0]]
            }
        }

        candidates.min { it[0] }[1]
    }

    def nextSquares(Villager villager, int[] nodeXY, int degree, Map visitedSquares) {

        def (int nodeX, int nodeY) = nodeXY

        def nextSquares = []

        def nodeNetwork = Model.model.nodeNetwork as Node[][]
        def node = nodeNetwork[nodeX][nodeY]

        final def squareProbabilities = Model.model.squareProbabilitiesForDegrees[degree]

        squareProbabilities.each { def square ->
            if (!visitedSquares.containsKey(square)) {
                def (int sX, int sY) = square[0]
                def nX = nodeX + sX
                def nY = nodeY + sY
                def neighbor = nodeNetwork[nX][nY] as Node
                TravelType travelType = neighbor.travelType
                def squareProbability = square[1] as Double

                if (villager.canTravel(travelType) && squareProbability > 0) {
                    def travelModifierMap = Model.travelModifier as Map<TravelType, Double>
                    int heightDifference = neighbor.height - node.height
                    Double travelModifier = travelModifierMap[travelType]
                    Double heightModifier = (heightDifference > 0
                            ? travelModifierMap[TravelType.UP_HILL]
                            : heightDifference == 0
                            ? travelModifierMap[TravelType.EVEN]
                            : travelModifierMap[TravelType.DOWN_HILL]) as Double
                    Double probability = (1 / (heightModifier * travelModifier)) * squareProbability
                    nextSquares << [probability, square[0]]
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
}
