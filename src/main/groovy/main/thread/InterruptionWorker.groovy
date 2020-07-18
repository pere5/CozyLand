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

    def update() {
        for (Villager villager: Model.villagers) {
            def model = villagerModel[villager.id]
            if (model) {
                if (villager.role?.id != model.roleId) {
                    model.roleId = villager.role?.id
                    villager.interrupt()
                }
            } else {
                villagerModel[villager.id] = [
                        roleId: villager.role?.id
                ]
            }
        }
    }
}
