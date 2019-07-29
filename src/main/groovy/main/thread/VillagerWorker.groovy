package main.thread

import main.Model
import main.villager.Villager

class VillagerWorker extends Worker {

    @Override
    def run() {
        super.intendedFps = 1
        super.run()
    }

    @Override
    def update() {
        for (Villager villager: Model.villagers) {

        }
    }
}
