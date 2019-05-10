package main.thread

import main.Main
import main.Model
import main.villager.Villager
import org.junit.BeforeClass
import org.junit.Test

class Tests {

    @BeforeClass
    static void setUp() throws Exception {
        Main.VIEWPORT_WIDTH = Main.WINDOW_WIDTH - (0)
        Main.VIEWPORT_HEIGHT = Main.WINDOW_HEIGHT - (22)
        Main.MAP_WIDTH = Main.VIEWPORT_WIDTH * 2
        Main.MAP_HEIGHT = Main.VIEWPORT_HEIGHT * 2

        Model.model.squareProbabilitiesForDegrees = Model.calculateProbabilitiesModel()
        Model.model.nodeNetwork = Model.generateBackground('lol.png')
    }

    @Test
    void degreeRange() {
        assert Model.degreeRange(45) == (315..359) + (0..135)
        assert Model.degreeRange(100) == 10..190
        assert Model.degreeRange(300) == (210..359) + (0..30)
    }

    @Test
    void probabilitiesModel() {

        def test = [359, 0 ,1, 89, 90, 91, 134, 135, 136, 179, 180, 181, 224, 225, 226, 269, 270 ,271, 314, 315, 316]
        test.each { def degree ->
            def degreeRange = Model.degreeRange(degree)
            def degreeProbabilities = Model.degreeProbabilities(degreeRange)
            def squares = Model.squareProbabilities(degreeProbabilities)

            assert degreeProbabilities.collect { it[0] } == degreeRange

            assert Math.abs((degreeProbabilities.sum { it[1] } as Double) - 100) < 0.00000001

            assert Math.abs((squares.collect { it[1] }.sum() as Double) - 100) < 0.00000001

            assert reverseEngineerDegree(degree, squares) < 1.6
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
        def pfw = new PathfinderWorker()

        def nextSquares1 = pfw.nextSquares(
                new Villager(),
                Model.pixelToNodeIdx([579, 341] as int[]),
                Model.pixelToNodeIdx([579 + 20, 341 + 20] as int[]),
                45,
                [:]
        )
        def nextSquares2 = pfw.nextSquares(
                new Villager(),
                Model.pixelToNodeIdx([592, 376] as int[]),
                Model.pixelToNodeIdx([592 + 20, 376 + 20] as int[]),
                45,
                [:]
        )
        def nextSquares3 = pfw.nextSquares(
                new Villager(),
                Model.pixelToNodeIdx([662, 208] as int[]),
                Model.pixelToNodeIdx([662 + 20, 208 + 20] as int[]),
                45,
                [:]
        )

        assert !nextSquares3

        assert nextSquares2.size() == 4
        def s2 = nextSquares2.collect { it[0][1] - it[0][0] }
        assert Math.abs(s2.sum() - 100) < 0.00000001
        assert s2.collect { Model.round(it) } == [8, 27, 34, 30]

        assert nextSquares1.size() == 5
        def s1 = nextSquares1.collect { it[0][1] - it[0][0] }
        assert Math.abs(s1.sum() - 100) < 0.00000001
        assert s1.collect { Model.round(it) } == [7, 27, 31, 27, 8]
    }
}