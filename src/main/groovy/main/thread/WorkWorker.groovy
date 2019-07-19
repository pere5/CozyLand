package main.thread

import main.Model
import main.TestPrints
import main.villager.Action
import main.villager.Villager

class WorkWorker extends Worker {

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
