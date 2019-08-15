package main.thread

import main.Model
import main.TestPrints
import main.villager.Action
import main.villager.Villager

class WorkWorker extends Worker {

    @Override
    def run() {
        super.intendedFps = 16
        super.run()
    }

    def update() {
        for (Villager villager: Model.villagers) {
            if (villager.workWorker) {
                def resolution = villager.work()
                if (resolution == Action.DONE) {
                    villager.toRuleWorker()
                    TestPrints.clearPrints(villager)
                }
            }
        }
    }
}
