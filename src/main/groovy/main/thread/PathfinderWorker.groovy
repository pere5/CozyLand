package main.thread

import javaSrc.linkedbinarytree.LinkedBinaryTree
import javaSrc.linkedbinarytree.Position
import main.Main
import main.Model
import main.Model.TravelType
import main.model.Tile
import main.things.Artifact
import main.things.Drawable
import main.villager.StraightPath
import main.villager.Villager

import java.awt.*
import java.util.List
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue

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

        for (Villager villager : Model.model.villagers) {

            if (villager.pathfinderWorker) {

                def pixelDest = Model.generateXY()
                def pixelStart = [villager.x, villager.y] as Double[]

                def tileStartXY = Model.pixelToTileIdx(pixelStart)
                def tileDestXY = Model.pixelToTileIdx(pixelDest)

                def perStarList = perStar(tileStartXY, tileDestXY, villager)

/*
                def tiles = longestPossibleBresenhams(idx)
*/
                if (perStarList) {
                    def pixels = perStarList.collect {
                        Model.tileToPixelIdx(it)
                    } as Double[][]

                    //def pixels2 = [[villager.x, villager.y] as Double[], Model.generateXY()] as Double[][]
                    //villager.actionQueue << new Wait()
                    for (int i = 0; i < pixels.length - 1; i++) {
                        def a = pixels[i]
                        def b = pixels[i + 1]
                        villager.actionQueue << new StraightPath(a, b)
                        //perTilesWithBresenham(a, b, villager)
                    }
                }

                villager.toWorkWorker()
            }
        }
    }

    List<int[]> perStar(int[] tileStart, int[] tileDest, Villager villager) {

        //visited is broken!
        Set<List<Integer>> visited = new HashSet<>()
        Queue<Position<int[]>> queue = new LinkedList<>()
        LinkedBinaryTree<int[]> lbt = new LinkedBinaryTree<>()

        List<List<Integer>> testList = []

        def rootPos = lbt.addRoot(tileStart)
        visited << [tileStart[0], tileStart[1]]
        testList << [tileStart[0], tileStart[1]]

        Position<int[]> stepPos = rootPos
        Position<int[]> deepestPath = rootPos
        int deepestDepth = 0

        int repetition = 0

        while (repetition < 500) {

            def idx = Model.bresenham(stepPos.element, tileDest, villager)

            if (idx != 0) {
                def nextStep = Model.bufferedBresenhamResultArray[idx]
                def currentStep = Model.bufferedBresenhamResultArray[idx - 1]
                def previousStep = idx >= 2 ? Model.bufferedBresenhamResultArray[idx - 2] : null

                if (nextStep == tileDest) {
                    queue << lbt.addLeft(stepPos, nextStep)
                    visited << [nextStep[0], nextStep[1]]
                    testList << [nextStep[0], nextStep[1]]
                    break
                }/* else if (currentStep && !visited.contains([currentStep[0], currentStep[1]])) {
                    queue << lbt.addLeft(stepPos, currentStep)
                    visited << [currentStep[0], currentStep[1]]
                    testList << [currentStep[0], currentStep[1]]
                }*/ else {
                    def (int[] left, int[] right) = findPath(nextStep, currentStep, previousStep, visited, villager)

                    if (left) {
                        queue << lbt.addLeft(stepPos, left)
                        visited << [left[0], left[1]]
                        testList << [left[0], left[1]]
                    }
                    if (right) {
                        queue << lbt.addRight(stepPos, right)
                        visited << [right[0], right[1]]
                        testList << [right[0], right[1]]
                    }
                }
            }

            if (!queue.peek()) break

            stepPos = queue.poll()
            def currentDepth = lbt.depth(stepPos)
            if (currentDepth > deepestDepth) {
                deepestPath = stepPos
                deepestDepth = currentDepth
            }

            repetition++
        }

        def retList = []

        int depth = lbt.depth(deepestPath)
        for (int i = 0; i <= depth; i++) {
            retList << stepPos.element
            stepPos = lbt.parent(stepPos)
        }
        Random rand = new Random()
        def randColor = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat())
        (Model.model.drawables as ConcurrentLinkedQueue<Drawable>).removeAll { it.parent == villager.id }

        testList.collect{ Model.tileToPixelIdx(it) }.each {
            Model.model.drawables << new Artifact(
                    size: 3, parent: villager.id, x: it[0], y: it[1],
                    color: randColor
            )
        }

        return retList.reverse()
        //return testList
    }

    private List<int[]> findPath(int[] nextStep, int[] currentStep, int[] previousStep, Set<List<Integer>> visited, Villager villager) {

        def tileNetwork = Model.model.tileNetwork as Tile[][]
        def ctl = Model.circularTileList as List<int[]>

        def delta = [nextStep[0] - currentStep[0], nextStep[1] - currentStep[1]] as int[]
        def deltaIdx = ctl.findIndexOf { it == delta }

        int[] right = null
        for (int i = deltaIdx + 1; i < deltaIdx + ctl.size(); i++) {
            def n = [currentStep[0] + ctl.get(i)[0], currentStep[1] + ctl.get(i)[1]] as int[]
            def tile = tileNetwork[n[0]][n[1]]
            if (n == previousStep) break
            if (n != nextStep
                    && villager.canTravel(tile.travelType)
                    && !visited.contains([n[0], n[1]])
            ) {
                right = n
                break
            }
        }
        int[] left = null
        for (int i = deltaIdx - 1; i > deltaIdx - ctl.size(); i--) {
            def n = [currentStep[0] + ctl[i][0], currentStep[1] + ctl[i][1]] as int[]
            def tile = tileNetwork[n[0]][n[1]]
            if (n == previousStep) break
            if (n != nextStep
                    && villager.canTravel(tile.travelType)
                    && !visited.contains([n[0], n[1]])
            ) {
                left = n
                break
            }
        }

        if ((left && right && left != right) || ((left && !right) || (!left && right))) {
            return [left, right]
        } else if (left && right && left == right) {
            return [left, null]
        } else {
            return [null, null]
        }
    }

    int[][] longestPossibleBresenhams(int i) {
        []
    }


    private void perTilesWithBresenham(Double[] pixelA, Double[] pixelB, Villager villager) {
        def tileDestXY = Model.pixelToTileIdx(pixelB)
        def pixelStep = pixelA
        def there = false

        while (!there) {

            def degree = Model.calculateDegreeRound(pixelStep, pixelB)
            def tileStartXY = Model.pixelToTileIdx(pixelStep)

            def nextTiles = nextTilesWithBresenham(villager, tileStartXY, tileDestXY, degree)

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

    def nextTilesWithBresenham(Villager villager, int[] tileStartXY, int[] tileDestXY, int degree) {

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
                        def idx = Model.bresenham(neighborXY, tileDestXY, villager)
                        def xy = Model.bufferedBresenhamResultArray[idx]
                        if (xy == tileDestXY) {
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
