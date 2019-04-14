package main.thread

import main.Model
import main.villager.Villager

class WorkWorker extends Worker {

    def update() {
        for (Villager villager : Model.model.villagers) {
            boolean working = villager.work()
            if (!working && !(villager in Model.model.lookingForWork)) {
                Model.model.lookingForWork << villager
            }
        }
    }
}
