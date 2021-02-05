package main.thread

import main.Model
import main.model.Villager

class InterruptionWorker extends Worker {

    Map<Integer, Map> villagerModel = [:]

    @Override
    def run() {
        super.intendedFps = 1
        super.run()
    }

    //interrupt if new role, abort all actions
    def update() {

    }
}
