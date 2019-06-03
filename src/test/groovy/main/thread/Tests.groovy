package main.thread

import main.Main
import main.Model
import main.Tile
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

        360.times { def degree ->
            def degreeRange = Model.degreeRange(degree)
            def degreeProbabilities = Model.degreeProbabilities(degreeRange)
            def squares = Model.squareProbabilities(degreeProbabilities)

            assert degreeProbabilities.collect { it[0] } == degreeRange

            assert Math.abs((degreeProbabilities.sum { it[1] } as Double) - 100) < 0.00000001

            assert Math.abs((squares.collect { it[1] }.sum() as Double) - 100) < 0.00000001

            assert reverseEngineerDegree(degree, squares) < 1.55
        }
    }

    static Double reverseEngineerDegree(int realDegree, def squares) {

        def vectors = squares.collect { def square ->
            [square[1] * square[0][0], square[1] * square[0][1]]
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
                [new Tile(height: 10, size: sw, x: 0, y: 0, travelType: p), new Tile(height: 10, size: sw, x: 0, y: 1, travelType: p), new Tile(height: 10, size: sw, x: 0, y: 2, travelType: p), new Tile(height: 10, size: sw, x: 0, y: 3, travelType: p), new Tile(height: 10, size: sw, x: 0, y: 4, travelType: p)],
                [new Tile(height: 10, size: sw, x: 1, y: 0, travelType: p), new Tile(height: 10, size: sw, x: 1, y: 1, travelType: p), new Tile(height: 10, size: sw, x: 1, y: 2, travelType: p), new Tile(height: 10, size: sw, x: 1, y: 3, travelType: p), new Tile(height: 10, size: sw, x: 1, y: 4, travelType: p)],
                [new Tile(height: 10, size: sw, x: 2, y: 0, travelType: w), new Tile(height: 10, size: sw, x: 2, y: 1, travelType: p), new Tile(height: 10, size: sw, x: 2, y: 2, travelType: p), new Tile(height: 10, size: sw, x: 2, y: 3, travelType: p), new Tile(height: 10, size: sw, x: 2, y: 4, travelType: p)],
                [new Tile(height: 10, size: sw, x: 3, y: 0, travelType: p), new Tile(height: 10, size: sw, x: 3, y: 1, travelType: p), new Tile(height: 10, size: sw, x: 3, y: 2, travelType: p), new Tile(height: 10, size: sw, x: 3, y: 3, travelType: w), new Tile(height: 10, size: sw, x: 3, y: 4, travelType: p)],
                [new Tile(height: 10, size: sw, x: 4, y: 0, travelType: w), new Tile(height: 10, size: sw, x: 4, y: 1, travelType: p), new Tile(height: 10, size: sw, x: 4, y: 2, travelType: w), new Tile(height: 10, size: sw, x: 4, y: 3, travelType: p), new Tile(height: 10, size: sw, x: 4, y: 4, travelType: p)]
        ]

        def pfw = new PathfinderWorker()

        def nextSquares1 = pfw.nextSquares(
                new Villager(),
                [0, 0] as int[],
                [1, 0] as int[],
                0
        )
        def nextSquares2 = pfw.nextSquares(
                new Villager(),
                [3, 0] as int[],
                [1, 3] as int[],
                90
        )
        def nextSquares3 = pfw.nextSquares(
                new Villager(),
                [3, 2] as int[],
                [4, 3] as int[],
                45
        )
        def nextSquares4 = pfw.nextSquares(
                new Villager(),
                [0, 1] as int[],
                [1, 1] as int[],
                0
        )

        assert !nextSquares3
/*

        assert nextSquares2.size() == 3
        def s2 = nextSquares2.collect { it[0][1] - it[0][0] }
        assert Math.abs(s2.sum() - 100) < 0.00000001
        assert s2.collect { Model.round(it) } == [12, 39, 49]
*/

        assert nextSquares1.size() == 5
        def s1 = nextSquares1.collect { it[0][1] - it[0][0] }
        assert Math.abs(s1.sum() - 100) < 0.00000001
        assert s1.collect { Model.round(it) } == [7, 27, 31, 27, 8]
    }
}