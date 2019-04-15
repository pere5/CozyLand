package main.thread

import main.Model
import main.villager.Villager

class WorkWorker extends Worker {

    def update() {
        (Model.model.villagers as List<Villager>).grep { !it.lookingForWork }.each { def villager ->
            boolean working = villager.work()
            if (!working) {
                villager.lookingForWork = true
            }
        }
    }
}
