package main.thread

import javaSrc.linkedbinarytree.LinkedBinaryTree
import javaSrc.linkedbinarytree.Position
import main.Main
import main.Model
import main.Model.TravelType
import main.TestPrints
import main.model.StraightPath
import main.model.Tile
import main.model.Villager
import main.npcLogic.action.WalkAction
import main.utility.BresenhamUtils
import main.utility.Utility

class PathfinderWorker extends Worker {

    static int[][] bresenhamBuffer = new int[Main.MAP_WIDTH + Main.MAP_HEIGHT][2]

    /*
        - [ ] Bresenham binary search per star algorithm, "optimized random path":
            - [x] Kör en perStar mot målet
            - [x] Spara perStar i en lista
            - [ ] Förbättring 1:
                - [ ] Ändra bresenham till att innehålla visited.
                - [ ] perStar: Om slut på element i kön, backa upp från alla löv i trädet, ett i taget och lägg på kön.
            - [ ] Förbättring 2:
                - [ ] Binärsök i listan så långa bresenham steg som möjligt.
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

                def walkAction = villager.actionQueue.find {it instanceof WalkAction && !it.initialized} as WalkAction
                if (walkAction) {
                    walkAction.initialized = true
                    def tileStart = villager.getTileXY()
                    def tileDest = walkAction.tileDest
                    if (tileStart == tileDest) {
                        continue
                    }
                    planPath(tileStart, tileDest, villager, walkAction)
                }

                TestPrints.printBresenhamMisses(villager)
                villager.toWorkWorker()
            }
        }
    }

    private void planPath(int[] tileStart, int[] tileDest, Villager villager, WalkAction walkAction) {
        def perStarTiles = perStar(tileStart, tileDest, villager)

        //def tiles = longestPossibleBresenhams(idx)

        def first = [villager.x, villager.y] as Double[]

        def result = []

        for (int i = 0; i < perStarTiles.size() - 1; i++) {
            def aT = perStarTiles[i]
            def bT = perStarTiles[i + 1]
            if (Utility.distance(aT, bT) > 2) {
                result.addAll(randomTilesWithBresenham(aT, bT, villager))
            } else {
                result.addAll([aT, bT])
            }
        }
        def pxls = Utility.randomPlacesInTileList(result)
        for (int i = 0; i < pxls.size() - 1; i++) {
            def start = i == 0 ? first : pxls[i]
            def dest = pxls[i + 1]
            walkAction.pathQueue << new StraightPath(start, dest)
            TestPrints.straightPathTestPrints(start, dest, villager)
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

            def idx = BresenhamUtils.bresenham(stepPos.element, tileDest, PathfinderWorker.bresenhamBuffer, villager)

            def nextStep = PathfinderWorker.bresenhamBuffer[idx].clone()
            def currentStep = PathfinderWorker.bresenhamBuffer[idx - 1].clone()
            def previousStep = idx >= 2 ? PathfinderWorker.bresenhamBuffer[idx - 2].clone() : null

            if (nextStep == tileDest) {
                stepPos = lbt.addLeft(stepPos, nextStep)
                visited << [nextStep[0], nextStep[1]]
                foundIt = true
                break
            } else {
                def (int[] left, int[] right) = leftRight(nextStep, currentStep, previousStep, visited, villager)

                if (!visited.contains([currentStep[0], currentStep[1]])) {
                    stepPos = lbt.addLeft(stepPos, currentStep)
                    visited << [currentStep[0], currentStep[1]]
                }

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

        TestPrints.perStarTestPrints(tileStart, tileDest, villager, visited)

        /*
        for (int i = 0; i < retList.size() - 1; i++) {
            if (retList[i] == retList[i + 1]) {
                throw new PerIsBorkenException()
            }
        }
        */

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

    private static def okStep(int[] nextStep, int[] currentStep, int[] previousStep, int[] neighbor, Villager villager, Set<List<Integer>> visited) {

        boolean ok = true
        def tileNetwork = Model.tileNetwork as Tile[][]
        def n = [currentStep[0] + neighbor[0], currentStep[1] + neighbor[1]] as int[]
        if (n[0] >= 0 && n[1] >= 0 && n[0] < tileNetwork.length && n[1] < tileNetwork[0].length) {
            def tile = (Model.tileNetwork as Tile[][])[n[0]][n[1]]

            if (n == previousStep) {
                return [ok, null]
            }
            if (n != nextStep && n != currentStep && villager.canTravel(tile.travelType) && !visited.contains([n[0], n[1]])) {
                return [ok, n]
            }

            return [!ok, null]
        } else {
            return [!ok, null]
        }
    }

    int[][] longestPossibleBresenhams(int i) {
        []
    }


    private List<int[]> randomTilesWithBresenham(int[] tileStart, int[] tileDest, Villager villager) {
        def retList = [tileStart] as List<int[]>
        int[] tileStep = tileStart
        while (true) {
            def nextTileDirections = nextTilesWithBresenham(villager, tileStep, tileDest)

            def stuckInLoop = nextTileDirections.size() == 1 && retList.size() >= 4 && retList[-1] == retList[-3] && retList[-2] == retList[-4]

            if (nextTileDirections && !stuckInLoop) {
                def random = Math.random() * 100
                def nextTileDirection = nextTileDirections.find { random >= (it[0][0] as Double) && random <= (it[0][1] as Double) }
                tileStep = [tileStep[0] + nextTileDirection[1][0], tileStep[1] + nextTileDirection[1][1]] as int[]
                retList << tileStep
                if (Utility.closeEnoughTile(tileStep, tileDest)) {
                    retList << tileDest
                    break
                }
            } else {
                def idx = BresenhamUtils.bresenham(tileStep, tileDest, PathfinderWorker.bresenhamBuffer, villager)
                def middleIndex = ((idx / 2) as int)
                if (middleIndex > 0) {
                    def middleTileDest = PathfinderWorker.bresenhamBuffer[middleIndex].clone()
                    retList.addAll(randomTilesWithBresenham(tileStep, middleTileDest, villager))
                    tileStep = middleTileDest
                } else {
                    retList << tileDest
                    break
                }
            }
        }

        return retList
    }

    def nextTilesWithBresenham(Villager villager, int[] tileStart, int[] tileDest) {

        def degree = Utility.calculateDegreeRound(tileStart, tileDest)
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
                        def idx = BresenhamUtils.bresenham(neighborXY, tileDest, PathfinderWorker.bresenhamBuffer, villager)
                        def xy = PathfinderWorker.bresenhamBuffer[idx].clone()
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
