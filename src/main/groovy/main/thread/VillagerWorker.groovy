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
            /*
                Okej
                trädstruktur med ledarskapsnivåer
                en ledare lägger in rules i sin undersåters privata ruleLists

                 - [ ] Man måste tracka villagerna status för ress olika roller.
                 - [ ] Assigna roller baserat på villagers status i dess rules.
             */

            Model.roleTree
        }
    }
}
