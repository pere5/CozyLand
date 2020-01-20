package main.thread

import main.Model
import main.TestPrints
import main.action.Action
import main.model.Villager

class WorkWorker extends Worker {

    @Override
    def run() {
        super.intendedFps = 8
        super.run()
    }

    def update() {
        for (Villager villager: Model.villagers) {
            if (villager.workWorker) {

                boolean resolution

                def action = villager.actionQueue.peek()
                if (action) {

                    if (action.initialized) {
                        villager.toPathfinderWorker()
                        continue
                    }

                    def canContinue = action.doIt(villager)
                    if (canContinue) {
                        resolution = Action.CONTINUE
                    } else {
                        villager.actionQueue.poll()
                        if (villager.actionQueue.peek()) {
                            resolution = Action.CONTINUE
                        } else {
                            resolution = Action.DONE
                        }
                    }
                } else {
                    resolution = Action.DONE
                }

                if (resolution == Action.DONE) {
                    villager.toRuleWorker()
                    TestPrints.clearPrints(villager)
                }
            }
        }
    }
}
