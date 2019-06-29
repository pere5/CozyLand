package main


import main.thread.PathfinderWorker
import main.villager.Villager
import org.junit.BeforeClass
import org.junit.Test

class Tests {

    @BeforeClass
    static void setUp() throws Exception {}

    @Test
    void degreeRange() {
        assert Model.degreeRange(45) == (315..359) + (0..135)
        assert Model.degreeRange(100) == 10..190
        assert Model.degreeRange(300) == (210..359) + (0..30)
    }

    @Test
    void probabilitiesModel() {

        360.times { def realDegree ->
            def degreeRange = Model.degreeRange(realDegree)
            def degreeProbabilities = Model.degreeProbabilities(degreeRange)
            def (tiles, testC) = Model.tileProbabilities(degreeProbabilities)

            def probKeys = testC.flatten().collect{ it.p }.unique()
            def allTheSame = [
                    [0,            [(true) :1267, (false):181]],
                    [0.4861111111, [(false):1376, (true) :72]],
                    [0.5555555556, [(false):1376, (true) :72]],
                    [0.6756756757, [(false):1411, (true) :37]]
            ]
            def thisCount = probKeys.collect { def probKey ->
                [probKey, testC.flatten().collect{ it.p }.countBy { probKey == it }]
            }.sort{ it[0] }

            assert thisCount == allTheSame

            def gLeft = 0
            def gRight = 0
            def gMiddle = 0

            testC.each { def list ->

                def left = 0
                def right = 0
                def middle = 0

                def s = list[0].s
                assert list.size() == 181
                assert list[90].d == realDegree
                left += list[0..89].p.sum()
                right += list[91..180].p.sum()
                middle += list[90].p

                def resultTile = tiles.find { it[0] == s.value }
                assert Math.abs(resultTile[1] - (left + right + middle)) < 0.0000001

                gLeft += left
                gRight += right
                gMiddle += middle
            }

            assert Math.abs(gLeft - gRight) < 0.00000000001
            assert Math.abs((gLeft + gMiddle + gRight) - 100) < 0.0000001


            assert degreeProbabilities.collect { it[0] } == degreeRange

            assert degreeProbabilities[90][0] == realDegree

            assert degreeProbabilities[0..89].collect { it[1] }.sum() == degreeProbabilities[91..180].collect { it[1] }.sum()

            assert Math.abs((degreeProbabilities.sum { it[1] } as Double) - 100) < 0.00000001

            assert Math.abs((tiles.collect { it[1] }.sum() as Double) - 100) < 0.00000001

            def diffDegree = reverseEngineerDegree(realDegree, tiles)
            assert diffDegree < 0.349 && Model.round(diffDegree) == 0
        }
    }

    static Double reverseEngineerDegree(int realDegree, def tiles) {

        def vectors = tiles.collect { def tile ->
            //https://stackoverflow.com/questions/12280827/find-tanget-point-in-circle
            def sRad = Math.atan2(tile[0][1], tile[0][0])
            def x = Math.cos(sRad)
            def y = Math.sin(sRad)
            [tile[1] * x, tile[1] * y]
        }

        def addedVector = vectors.inject([0, 0]) { def result, def elem ->
            result[0] += elem[0]
            result[1] += elem[1]
            return result
        }

        def l = Math.toDegrees(Math.atan2(addedVector[1], addedVector[0]))

        def reversed = l >= 0 ? l : l + 360
        //https://gamedev.stackexchange.com/questions/4467/comparing-angles-and-working-out-the-difference
        def diffDeg = 180.0 - Math.abs(Math.abs(reversed - realDegree) - 180.0)

        return diffDeg
    }

    @Test
    void nextTiles() {
        Model.model.tileProbabilitiesForDegrees = Model.calculateProbabilitiesModel()
        plainTerrain()
        unevenTerrain()
    }

    private void plainTerrain() {
        def sw = Main.TILE_WIDTH
        def w = Model.TravelType.WATER
        def p = Model.TravelType.PLAIN

        Model.model.tileNetwork = [
                [new Tile(height: 10, size: sw, x: 0, y: 0, travelType: p), new Tile(height: 10, size: sw, x: 0, y: 1, travelType: p), new Tile(height: 10, size: sw, x: 0, y: 2, travelType: p), new Tile(height: 10, size: sw, x: 0, y: 3, travelType: p)],
                [new Tile(height: 10, size: sw, x: 1, y: 0, travelType: p), new Tile(height: 10, size: sw, x: 1, y: 1, travelType: p), new Tile(height: 10, size: sw, x: 1, y: 2, travelType: p), new Tile(height: 10, size: sw, x: 1, y: 3, travelType: p)],
                [new Tile(height: 10, size: sw, x: 2, y: 0, travelType: w), new Tile(height: 10, size: sw, x: 2, y: 1, travelType: p), new Tile(height: 10, size: sw, x: 2, y: 2, travelType: p), new Tile(height: 10, size: sw, x: 2, y: 3, travelType: p)],
                [new Tile(height: 10, size: sw, x: 3, y: 0, travelType: p), new Tile(height: 10, size: sw, x: 3, y: 1, travelType: p), new Tile(height: 10, size: sw, x: 3, y: 2, travelType: p), new Tile(height: 10, size: sw, x: 3, y: 3, travelType: w)],
                [new Tile(height: 10, size: sw, x: 4, y: 0, travelType: w), new Tile(height: 10, size: sw, x: 4, y: 1, travelType: p), new Tile(height: 10, size: sw, x: 4, y: 2, travelType: p), new Tile(height: 10, size: sw, x: 4, y: 3, travelType: p)]
        ]

        def pfw = new PathfinderWorker()

        def rightByWall = pfw.nextTilesWithBresenham(
                new Villager(),
                [0, 0] as int[],
                [1, 0] as int[],
                0
        )
        def upRoundWater = pfw.nextTilesWithBresenham(
                new Villager(),
                [3, 0] as int[],
                [3, 1] as int[],
                90
        )
        def diagonalBetweenWater = pfw.nextTilesWithBresenham(
                new Villager(),
                [3, 2] as int[],
                [4, 3] as int[],
                45
        )
        def free = pfw.nextTilesWithBresenham(
                new Villager(),
                [0, 1] as int[],
                [1, 1] as int[],
                0
        )
        def freeDiagonal = pfw.nextTilesWithBresenham(
                new Villager(),
                [1, 2] as int[],
                [2, 3] as int[],
                45
        )

        /*
            Rounded distribution: 13, 23, 29, 23, 13
         */

        def r = 12.777777778378333
        def rm = 22.77777777724835
        def m = 28.88888888874665
        def l = 12.777777778378336
        def lm = 22.777777777248346

        def rightByWallComp = rightByWall.collectEntries { [[it[1][0], it[1][1]], it[0][1] - it[0][0]] }
        def upRoundWaterComp = upRoundWater.collectEntries { [[it[1][0], it[1][1]], it[0][1] - it[0][0]] }
        def diagonalBetweenWaterComp = diagonalBetweenWater.collectEntries { [[it[1][0], it[1][1]], it[0][1] - it[0][0]] }
        def freeComp = free.collectEntries { [[it[1][0], it[1][1]], it[0][1] - it[0][0]] }
        def freeDiagonalComp = freeDiagonal.collectEntries { [[it[1][0], it[1][1]], it[0][1] - it[0][0]] }

        def rightByWallExpect = [[0, 1]: l, [1, 1]: lm, [1, 0]: m].each {
            it.value = it.value * (100 / (l + lm + m))
        }
        def upRoundWaterExpect = [[-1, 1]: lm, [0, 1]: m, [1, 1]: rm].each {
            it.value = it.value * (100 / (lm + m + rm))
        }
        def diagonalBetweenWaterExpect = [[1, 1]: m, [1, 0]: rm, [1, -1]: r].each {
            it.value = it.value * (100 / (m + rm + r))
        }
        def freeExpect = [[0, 1]: l, [1, 1]: lm, [1, 0]: m, [0, -1]: r, [1, -1]: rm]
        def freeDiagonalExpect = [[-1, 1]: l, [0, 1]: lm, [1, 1]: m, [1, 0]: rm, [1, -1]: r]

        def e = 0.0000000001 //in probability percent

        assert rightByWallComp.each { assert Math.abs(rightByWallExpect[it.key] - it.value) < e }
        assert upRoundWaterComp.each { assert Math.abs(upRoundWaterExpect[it.key] - it.value) < e }
        assert diagonalBetweenWaterComp.each { assert Math.abs(diagonalBetweenWaterExpect[it.key] - it.value) < e }
        assert freeComp.each { assert Math.abs(freeExpect[it.key] - it.value) < e }
        assert freeDiagonalComp.each { assert Math.abs(freeDiagonalExpect[it.key] - it.value) < e }
    }

    private void unevenTerrain() {
        def sw = Main.TILE_WIDTH
        def tm = Model.travelModifier
        def w = Model.TravelType.WATER
        def p = Model.TravelType.PLAIN
        def h = Model.TravelType.HILL
        def ro = Model.TravelType.ROAD
        def u = Model.TravelType.UP_HILL

        Model.model.tileNetwork = [
                [new Tile(height: 10, size: sw, x: 0, y: 0, travelType: p), new Tile(height: 20, size: sw, x: 0, y: 1, travelType: p),  new Tile(height: 10, size: sw, x: 0, y: 2, travelType: p),  new Tile(height: 10, size: sw, x: 0, y: 3, travelType: p)],
                [new Tile(height: 20, size: sw, x: 1, y: 0, travelType: p), new Tile(height: 10, size: sw, x: 1, y: 1, travelType: p),  new Tile(height: 10, size: sw, x: 1, y: 2, travelType: p),  new Tile(height: 10, size: sw, x: 1, y: 3, travelType: p)],
                [new Tile(height: 10, size: sw, x: 2, y: 0, travelType: w), new Tile(height: 10, size: sw, x: 2, y: 1, travelType: h),  new Tile(height: 20, size: sw, x: 2, y: 2, travelType: ro), new Tile(height: 10, size: sw, x: 2, y: 3, travelType: p)],
                [new Tile(height: 10, size: sw, x: 3, y: 0, travelType: p), new Tile(height: 10, size: sw, x: 3, y: 1, travelType: p),  new Tile(height: 10, size: sw, x: 3, y: 2, travelType: p),  new Tile(height: 10, size: sw, x: 3, y: 3, travelType: w)],
                [new Tile(height: 10, size: sw, x: 4, y: 0, travelType: w), new Tile(height: 10, size: sw, x: 4, y: 1, travelType: ro), new Tile(height: 10, size: sw, x: 4, y: 2, travelType: p),  new Tile(height: 10, size: sw, x: 4, y: 3, travelType: p)]
        ]

        def pfw = new PathfinderWorker()

        def rightByWall = pfw.nextTilesWithBresenham(
                new Villager(),
                [0, 0] as int[],
                [1, 0] as int[],
                0
        )
        def upRoundWater = pfw.nextTilesWithBresenham(
                new Villager(),
                [3, 0] as int[],
                [3, 1] as int[],
                90
        )
        def freeDiagonal = pfw.nextTilesWithBresenham(
                new Villager(),
                [1, 2] as int[],
                [2, 3] as int[],
                45
        )

        /*
            Rounded distribution: 13, 23, 29, 23, 13
         */

        def r = 12.777777778378333
        def rm = 22.77777777724835
        def m = 28.88888888874665
        def l = 12.777777778378336
        def lm = 22.777777777248346

        def rightByWallComp = rightByWall.collectEntries { [[it[1][0], it[1][1]], it[0][1] - it[0][0]] }
        def upRoundWaterComp = upRoundWater.collectEntries { [[it[1][0], it[1][1]], it[0][1] - it[0][0]] }
        def freeDiagonalComp = freeDiagonal.collectEntries { [[it[1][0], it[1][1]], it[0][1] - it[0][0]] }

        def up = 1 / tm[u]
        def rightByWallExpect = [[0, 1]: l * up, [1, 1]: lm, [1, 0]: m * up].each {
            it.value = it.value * (100 / ((l * up) + lm + (m * up)))
        }

        def plain = 1 / tm[p]
        def road = 1 / tm[ro]
        def hill = 1 / tm[h]
        def upRoundWaterExpect = [[-1, 1]: lm * hill, [0, 1]: m * plain, [1, 1]: lm * road].each {
            it.value = it.value * (100 / ((lm * hill) + (m * plain) + (lm * road)))
        }

        def highRoad = 1 / (tm[ro] * tm[u])
        def freeDiagonalExpect = [[-1, 1]: l * plain, [0, 1]: lm * plain, [1, 1]: m * plain, [1, 0]: rm * highRoad, [1, -1]: r * hill].each {
            it.value = it.value * (100 / ((l * plain) + (lm * plain) + (m * plain) + (rm * highRoad) + (r * hill)))
        }

        def e = 0.0000000001 //in probability percent

        assert rightByWallComp.each { assert Math.abs(rightByWallExpect[it.key] - it.value) < e }
        assert upRoundWaterComp.each { assert Math.abs(upRoundWaterExpect[it.key] - it.value) < e }
        assert freeDiagonalComp.each { assert Math.abs(freeDiagonalExpect[it.key] - it.value) < e }
    }
}