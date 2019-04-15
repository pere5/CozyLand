package main.thread

import main.Model
import main.rule.Rule
import main.villager.Villager

class RuleWorker extends Worker {

    def update() {
        (Model.model.villagers as List<Villager>).grep { it.lookingForWork }.each { def villager ->
            def selectedRule = null

            int currentStatus = Integer.MAX_VALUE
            for (Rule rule : Model.model.rules) {
                int newStatus = rule.status(villager)
                if (newStatus < currentStatus || (newStatus == currentStatus && rule.rank > selectedRule.rank)) {
                    currentStatus = newStatus
                    selectedRule = rule
                }
            }
            if (selectedRule) {
                selectedRule.startWork(villager, currentStatus)
                villager.lookingForWork = false
            }
        }
    }
}
