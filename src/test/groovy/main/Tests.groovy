package main

import main.Main
import main.Model
import main.Tile
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
            def (squares, testC) = Model.squareProbabilities(degreeProbabilities)

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

                def resultSquare = squares.find { it[0] == s.value }
                assert Math.abs(resultSquare[1] - (left + right + middle)) < 0.0000001

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

            assert Math.abs((squares.collect { it[1] }.sum() as Double) - 100) < 0.00000001

            assert reverseEngineerDegree(realDegree, squares) < 0.349
        }
    }

    static Double reverseEngineerDegree(int realDegree, def squares) {

        def vectors = squares.collect { def square ->
            //https://stackoverflow.com/questions/12280827/find-tanget-point-in-circle
            def sRad = Math.atan2(square[0][1], square[0][0])
            def x = Math.cos(sRad)
            def y = Math.sin(sRad)
            [square[1] * x, square[1] * y]
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
    void nextSquares() {
        def sw = Main.SQUARE_WIDTH
        def w = Model.TravelType.WATER
        def p = Model.TravelType.PLAIN

        Model.model.squareProbabilitiesForDegrees = Model.calculateProbabilitiesModel()
        Model.model.tileNetwork = [
                [new Tile(height: 10, size: sw, x: 0, y: 0, travelType: p), new Tile(height: 10, size: sw, x: 0, y: 1, travelType: p), new Tile(height: 10, size: sw, x: 0, y: 2, travelType: p), new Tile(height: 10, size: sw, x: 0, y: 3, travelType: p)],
                [new Tile(height: 10, size: sw, x: 1, y: 0, travelType: p), new Tile(height: 10, size: sw, x: 1, y: 1, travelType: p), new Tile(height: 10, size: sw, x: 1, y: 2, travelType: p), new Tile(height: 10, size: sw, x: 1, y: 3, travelType: p)],
                [new Tile(height: 10, size: sw, x: 2, y: 0, travelType: w), new Tile(height: 10, size: sw, x: 2, y: 1, travelType: p), new Tile(height: 10, size: sw, x: 2, y: 2, travelType: p), new Tile(height: 10, size: sw, x: 2, y: 3, travelType: p)],
                [new Tile(height: 10, size: sw, x: 3, y: 0, travelType: p), new Tile(height: 10, size: sw, x: 3, y: 1, travelType: p), new Tile(height: 10, size: sw, x: 3, y: 2, travelType: p), new Tile(height: 10, size: sw, x: 3, y: 3, travelType: w)],
                [new Tile(height: 10, size: sw, x: 4, y: 0, travelType: w), new Tile(height: 10, size: sw, x: 4, y: 1, travelType: p), new Tile(height: 10, size: sw, x: 4, y: 2, travelType: w), new Tile(height: 10, size: sw, x: 4, y: 3, travelType: p)]
        ]

        def pfw = new PathfinderWorker()

        def rightByWall = pfw.nextSquares(
                new Villager(),
                [0, 0] as int[],
                [1, 0] as int[],
                0
        )
        def upRoundWater = pfw.nextSquares(
                new Villager(),
                [3, 0] as int[],
                [1, 3] as int[],
                90
        )
        def diagonalBetweenWater = pfw.nextSquares(
                new Villager(),
                [3, 2] as int[],
                [4, 3] as int[],
                45
        )
        def free = pfw.nextSquares(
                new Villager(),
                [0, 1] as int[],
                [1, 1] as int[],
                0
        )

        def rightByWallRecalc = rightByWall.collect {
            [it[0][1] - it [0][0], it[1][0], it[1][1]]
        }

        def rightByWallExpect = [[12.5, 0, 1], [25, 1, 1], [25, 1, 0]].collect {
            it[0] *= 100 / (25 + 25 + 12.5)
            return it
        }

        def freeRecalc = free.collect {
            [it[0][1] - it [0][0], it[1][0], it[1][1]]
        }

        def freeExpect = [[12.5, 0, 1], [25, 1, 1], [25, 1, 0], [12.5, 0, -1], [25, 1, -1]]

        assert freeRecalc == freeExpect

        assert rightByWallRecalc == rightByWallExpect
/*

        assert upRoundWater.size() == 3
        def s2 = upRoundWater.collect { it[0][1] - it[0][0] }
        assert Math.abs(s2.sum() - 100) < 0.00000001
        assert s2.collect { Model.round(it) } == [12, 39, 49]
*/

        assert rightByWall.size() == 5
        def s1 = rightByWall.collect { it[0][1] - it[0][0] }
        assert Math.abs(s1.sum() - 100) < 0.00000001
        assert s1.collect { Model.round(it) } == [7, 27, 31, 27, 8]
    }
}