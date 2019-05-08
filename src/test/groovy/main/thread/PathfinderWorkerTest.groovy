package main.thread

import main.Main
import main.Model
import main.villager.Villager
import org.junit.Before
import org.junit.Test

class PathfinderWorkerTest {

    @Before
    void setUp() throws Exception {
        Main.VIEWPORT_WIDTH = Main.WINDOW_WIDTH - (0)
        Main.VIEWPORT_HEIGHT = Main.WINDOW_HEIGHT - (22)
        Main.MAP_WIDTH = Main.VIEWPORT_WIDTH * 2
        Main.MAP_HEIGHT = Main.VIEWPORT_HEIGHT * 2

        Model.model.squareProbabilitiesForDegrees = Model.calculateProbabilitiesModel()
        Model.model.nodeNetwork = Model.generateBackground()
    }

    @Test
    void nextSquares() {
        def pfw = new PathfinderWorker()

        def nextSquares1 = pfw.nextSquares(
                new Villager(),
                Model.pixelToNodeIdx([579, 341] as int[]),
                45,
                [:]
        )
        def nextSquares2 = pfw.nextSquares(
                new Villager(),
                Model.pixelToNodeIdx([592, 376] as int[]),
                45,
                [:]
        )
        def nextSquares3 = pfw.nextSquares(
                new Villager(),
                Model.pixelToNodeIdx([662, 208] as int[]),
                45,
                [:]
        )


        //write this stuff?!?

        assert nextSquares1 && Math.abs(nextSquares1.last()[0][1] - 100) < 0.00000001

        int lol = 0

        assert lol == 0
    }
}