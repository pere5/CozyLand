package main.npcLogic.action

import main.Model
import main.exception.PerIsBorkenException
import main.model.Villager
import main.npcLogic.Action
import main.things.building.home.House
import main.things.building.home.Hut
import main.things.building.home.Temple
import main.things.building.home.Tent

class HomeAction extends Action {

    Model.Shape shape
    Boolean onlyWait = Boolean.FALSE

    HomeAction(int seconds, Model.Shape shape) {
        this.seconds = seconds
        this.shape = shape
    }

    @Override
    boolean interrupt() {
        return false
    }

    @Override
    void switchWorker(Villager me) {
        throw new UnsupportedOperationException()
    }

    @Override
    boolean doIt(Villager me) {
        if (onlyWait) {
            def resolution = waitForPeriod()
            return resolution
        } else {
            def tileNetwork = Model.tileNetwork
            int depth = 0
            while (true) {
                def tileXY = positionByGoldenRatio(me, me.role.tribe.location.tileXY, me.role.tribe.buildings.size() + depth)
                def travelType = tileNetwork[tileXY[0]][tileXY[1]].travelType
                if (travelType != Model.TravelType.MOUNTAIN && me.canTravel(travelType)) {
                    if (shape == Model.Shape.HUT) {
                        new Hut(me, tileXY)
                    } else if (shape == Model.Shape.TEMPLE) {
                        new Temple(me, tileXY)
                    } else if (shape == Model.Shape.TENT) {
                        new Tent(me, tileXY)
                    } else if (shape == Model.Shape.HOUSE) {
                        new House(me, tileXY)
                    } else {
                        throw new PerIsBorkenException()
                    }
                    break
                } else {
                    depth++
                    if (depth > 20) {
                        onlyWait = true
                        return CONTINUE
                    }
                }
            }
            return DONE
        }
    }

    private int[] positionByGoldenRatio(Villager me, int[] tileXY, int multiplier) {
        def angle = multiplier * (2 * Math.PI) * Model.GOLDEN_RATIO
        def distance = Math.log(me.role.tribe.buildings.size() + 2) * 2 + 1
        def pointX = (tileXY[0] + distance * Math.cos(angle)) as Integer
        def pointY = (tileXY[1] + distance * Math.sin(angle)) as Integer
        return [pointX, pointY] as int[]
    }
}
