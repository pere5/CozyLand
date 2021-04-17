package main.thread

import main.Model
import main.TestPrints
import main.model.Villager
import main.npcLogic.Action

class WorkWorker extends Worker {

    @Override
    def run() {
        super.intendedFps = 20
        super.run()
    }

    def update() {
        for (Villager villager: Model.villagers) {
            if (villager.workWorker) {

                boolean resolution

                def action = villager.actionQueue.peek()

                if (action && !action.timerStarted) {
                    action.timer = System.currentTimeMillis()
                    action.timerStarted = true
                }

                if (action && !action.initializeByAnotherWorker) {
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
                } else if (action && action.initializeByAnotherWorker) {
                    action.switchWorker(villager)
                    resolution = Action.CONTINUE
                } else {
                    resolution = Action.DONE
                }

                if (resolution == Action.DONE) {
                    if (action.timer <= System.currentTimeMillis() - 1000) {
                        villager.toRuleWorker()
                        TestPrints.clearPrints(villager)
                    }
                }
            }
        }
    }
}
