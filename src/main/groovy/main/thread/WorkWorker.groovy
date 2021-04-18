package main.thread

import main.Model
import main.TestPrints
import main.exception.PerIsBorkenException
import main.model.Villager
import main.npcLogic.Action

class WorkWorker extends Worker {

    enum Resolution { DONE, CONTINUE }

    @Override
    def run() {
        super.intendedFps = 20
        super.run()
    }

    def update() {
        for (Villager villager: Model.villagers) {
            if (villager.workWorker) {
                Resolution resolution
                def action = villager.actionQueue.peek()
                if (action && action.suspend) {
                    if (action.timer < System.currentTimeMillis() - 2000) {
                        resolution = nextAction(villager)
                    } else {
                        println(action.timer - (System.currentTimeMillis() - 2000))
                        resolution = Resolution.CONTINUE
                    }
                } else if (action && action.initializeByAnotherWorker) {
                    action.switchWorker(villager)
                    resolution = Resolution.CONTINUE
                } else if (action && !action.initializeByAnotherWorker) {
                    Action.Resolution actionResolution = action.doIt(villager)
                    if (actionResolution == Action.Resolution.CONTINUE) {
                        resolution = Resolution.CONTINUE
                    } else if (actionResolution == Action.Resolution.DONE) {
                        resolution = nextAction(villager)
                    } else if (actionResolution == Action.Resolution.SUSPEND) {
                        action.suspend = true
                        resolution = Resolution.CONTINUE
                    } else {
                        throw new PerIsBorkenException()
                    }
                } else {
                    resolution = Resolution.DONE
                }

                if (resolution == Resolution.DONE) {
                    villager.toRuleWorker()
                    TestPrints.clearPrints(villager)
                }
            }
        }
    }

    private Resolution nextAction(Villager villager) {
        villager.actionQueue.poll()
        if (villager.actionQueue.peek()) {
            return Resolution.CONTINUE
        } else {
            return Resolution.DONE
        }
    }
}
