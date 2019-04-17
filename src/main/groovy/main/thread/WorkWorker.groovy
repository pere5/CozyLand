package main.thread

import main.Model
import main.villager.Action
import main.villager.Villager

class WorkWorker extends Worker {

    def update() {
        Model.model.villagers.grep { it.working }.each { Villager villager ->
            if (villager.work() == Action.DONE) {
                villager.inLookingForRule()
            }
        }
    }
}
