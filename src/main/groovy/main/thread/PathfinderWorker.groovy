package main.thread


import javaSrc.linkedbinarytree.LinkedBinaryTree
import javaSrc.linkedbinarytree.Position
import main.Main
import main.Model
import main.Model.TravelType
import main.TestPrints
import main.calculator.Path
import main.model.Tile
import main.villager.StraightPath
import main.villager.Villager

class PathfinderWorker extends Worker {

    /*
        - [ ] Bresenham binary search per star algorithm, "optimized random path":
            - [x] Kör en perStar mot målet
            - [x] Spara perStar i en lista
            - [ ] Ändra bresenham till att innehålla visited. Då kommer den att krocka och man får en cirkulär fanning out effect istället för en krypa längt kusten effekt
            - [ ] Binärsök i listan så långa bresenham steg som möjligt
            - [x] Spara punkterna och kör perTiles med bresenham mellan dem.

        - [x] kör steg nod för nod, för nästa steg:
          - [x] kör 90 grader med mittersta graden pekandes mot målet
          - [x] fördela ut graderna lika till grann tilesen
          - [x] hårdkoda det med en färdig lösning per grad för alla 360 grader.
          - [x] lägg upp en (normal) fördelning av sannolikhet för graderna över 90 grader.
          - [x] hårdkoda fördelningen för 0 -> 90 grader med max i 45
          - [x] beräkna genomsnittliga sannolikheten för varje grann nod relativt till de andra tiles utifrån gradernas sannolikheter
          - [x] omfördela sannolikheterna mot vaje nod baserat på tilens movementCost relativt till de andra tilsen
     */

    def update() {

        for (Villager villager : Model.villagers) {

            if (villager.pathfinderWorker) {

                def tileDest = villager.tileQueue.poll()
                def tileStart = Model.pixelToTileIdx([villager.x, villager.y] as Double[])
                while (tileDest) {

                    def psList = perStar(tileStart, tileDest, villager)

                    //def tiles = longestPossibleBresenhams(idx)

                    for (int i = 0; i < psList.size() - 1; i++) {
                        def aT = psList[i]
                        def bT = psList[i + 1]
                        def aP = Model.tileToPixelIdx(aT)
                        def bP = Model.tileToPixelIdx(bT)
                        if (Model.distance(aT, bT) > 2) {
                            def perTiles = perTilesWithBresenham(aT, bT, villager)
                            for (int j = 0; j < perTiles.size() - 1; j++) {
                                aP = Model.tileToPixelIdx(perTiles[j])
                                bP = Model.tileToPixelIdx(perTiles[j + 1])

                                //random place in tile here somewhere

                                villager.actionQueue << new StraightPath(aP, bP, villager)
                            }
                        } else {
                            villager.actionQueue << new StraightPath(aP, bP, villager)
                        }
                    }

                    tileDest = villager.tileQueue.poll()
                }

                TestPrints.printBresenhamMisses(villager)

                villager.toWorkWorker()
            }
        }
    }

    List<int[]> perStar(int[] tileStart, int[] tileDest, Villager villager) {

        Set<List<Integer>> visited = new LinkedHashSet<>()
        Queue<Position<int[]>> queue = new LinkedList<>()
        LinkedBinaryTree<int[]> lbt = new LinkedBinaryTree<>()

        def rootPos = lbt.addRoot(tileStart)
        visited << [tileStart[0], tileStart[1]]

        Position<int[]> stepPos = rootPos
        Position<int[]> deepestPath = rootPos
        int deepestDepth = 0
        def foundIt = false

        while (true) {

            def idx = Path.bresenham(stepPos.element, tileDest, villager)

            def nextStep = Path.bresenhamBuffer[idx].clone()
            def currentStep = Path.bresenhamBuffer[idx - 1].clone()
            def previousStep = idx >= 2 ? Path.bresenhamBuffer[idx - 2].clone() : null

            /*
                Bug: nextStep is reachable by bresenham but current step might not be.
                This causes trouble for perTilesWithBresenham.
                Instead of fixing this side of the problem,
                we add a jump in perTilesWithBresenham:
                    retList << tileDest
                    break
             */

            if (nextStep == tileDest) {
                stepPos = lbt.addLeft(stepPos, nextStep)
                visited << [nextStep[0], nextStep[1]]
                foundIt = true
                break
            } else {
                def (int[] left, int[] right) = leftRight(nextStep, currentStep, previousStep, visited, villager)

                stepPos = lbt.addLeft(stepPos, currentStep)
                visited << [currentStep[0], currentStep[1]]

                if (left) {
                    queue << lbt.addLeft(stepPos, left)
                    visited << [left[0], left[1]]
                }
                if (right) {
                    queue << lbt.addRight(stepPos, right)
                    visited << [right[0], right[1]]
                }
            }

            if (!queue.peek()) break

            stepPos = queue.poll()
            def currentDepth = lbt.depth(stepPos)
            if (currentDepth > deepestDepth) {
                deepestPath = stepPos
                deepestDepth = currentDepth
            }
        }

        def retList = []

        def path = foundIt ? stepPos : deepestPath

        while (true) {
            if (path) {
                retList << path.element
                path = lbt.parent(path)
            } else {
                break
            }
        }

        TestPrints.testPrints(tileStart, tileDest, villager, visited)

        /*def s = allPoints.size()
        if (allPoints.unique().size() != s) {
            throw new PerIsBorkenException()
        }*/

        return retList.reverse()
        //return allPoints
    }

    List<int[]> leftRight(int[] nextStep, int[] currentStep, int[] previousStep, Set<List<Integer>> visited, Villager villager) {

        def ctl = Model.circularTileList as List<int[]>

        def delta = [nextStep[0] - currentStep[0], nextStep[1] - currentStep[1]] as int[]
        def deltaIdx = ctl.findIndexOf { it == delta }

        int[] right = null
        for (int i = deltaIdx + 1; i < deltaIdx + 4; i++) {
            def (boolean ok, result) = okStep(nextStep, currentStep, previousStep, ctl.get(i) as int[], villager, visited)
            if (ok) {
                right = result as int[]
                break
            }
        }

        int[] left = null
        for (int i = deltaIdx - 1; i > deltaIdx - 4; i--) {
            def (boolean ok, result) = okStep(nextStep, currentStep, previousStep, ctl.get(i) as int[], villager, visited)
            if (ok) {
                left = result as int[]
                break
            }
        }

        if (left && right && left == right) {
            return [left, null]
        } else {
            return [left, right]
        }
    }

    private def okStep(int[] nextStep, int[] currentStep, int[] previousStep, int[] neighbor, Villager villager, Set<List<Integer>> visited) {

        boolean ok = true

        def n = [currentStep[0] + neighbor[0], currentStep[1] + neighbor[1]] as int[]
        def tile = (Model.tileNetwork as Tile[][])[n[0]][n[1]]

        if (n == previousStep) {
            return [ok, null]
        }
        if (n != nextStep && n != currentStep && villager.canTravel(tile.travelType) && !visited.contains([n[0], n[1]])) {
            return [ok, n]
        }

        return [!ok, null]
    }

    int[][] longestPossibleBresenhams(int i) {
        []
    }


    private List<int[]> perTilesWithBresenham(int[] tileStart, int[] tileDest, Villager villager) {
        def retList = [tileStart] as List<int[]>
        int[] tileStep = tileStart
        while (true) {
            def nextTileDirections = nextTilesWithBresenham(villager, tileStep, tileDest)
            if (nextTileDirections) {
                def random = Math.random() * 100
                def nextTileDirection = nextTileDirections.find { random >= (it[0][0] as Double) && random <= (it[0][1] as Double) }
                tileStep = [tileStep[0] + nextTileDirection[1][0], tileStep[1] + nextTileDirection[1][1]] as int[]
                retList << tileStep
                if (StraightPath.closeEnoughTile(tileStep, tileDest)) {
                    retList << tileDest
                    break
                }
            } else {
                retList << tileDest
                break
            }
        }

        return retList
    }

    Double[] randomPlaceInTile(int[] tile) {
        def pixelIdx = Model.tileToPixelIdx(tile)
        pixelIdx[0] += 1
        pixelIdx[1] += 1
        pixelIdx[0] += (Main.TILE_WIDTH - 2) * Math.random()
        pixelIdx[1] += (Main.TILE_WIDTH - 2) * Math.random()
        return pixelIdx
    }

    def nextTilesWithBresenham(Villager villager, int[] tileStart, int[] tileDest) {

        def degree = Model.calculateDegreeRound(tileStart, tileDest)
        def (int tileX, int tileY) = tileStart

        def nextTiles = []

        def tileNetwork = Model.tileNetwork as Tile[][]
        def tile = tileNetwork[tileX][tileY]

        final def tileProbabilities = Model.tileProbabilitiesForDegrees[degree]

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
                        def idx = Path.bresenham(neighborXY, tileDest, villager)
                        def xy = Path.bresenhamBuffer[idx].clone()
                        if (xy == tileDest) {
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
