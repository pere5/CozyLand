package main.thread

import main.Model
import main.rule.Rule
import main.villager.Villager

class RuleWorker extends Worker {

    def update() {
        for (Villager villager : Model.model.lookingForWork) {
            def selectedRule = null

            int currentStatus = Integer.MAX_VALUE
            for (Rule rule : Model.model.rules) {
                int newStatus = rule.calculateStatus(villager)
                if (newStatus < currentStatus || (newStatus == currentStatus && rule.rank > selectedRule.rank)) {
                    currentStatus = newStatus
                    selectedRule = rule
                }
            }
            if (selectedRule) {
                selectedRule.initWork(villager, currentStatus)
            }
        }
    }
}
