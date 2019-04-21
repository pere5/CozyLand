package main.thread

import main.Model
import main.villager.Action
import main.villager.Villager

class WorkWorker extends Worker {

    def update() {
        (Model.model.villagers as List<Villager>).grep { it.workWorker }.each { Villager villager ->
            def resolution = villager.work()
            if (resolution == Action.DONE) {
                villager.toRuleWorker()
            }
        }
    }
}
