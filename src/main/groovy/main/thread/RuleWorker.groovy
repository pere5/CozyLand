package main.thread

import main.Model
import main.rule.Rule
import main.villager.Villager

class RuleWorker extends Worker {

    def update() {
        (Model.model.villagers as List<Villager>).grep { it.lookingForWork }.each { def villager ->
            def rule = null
            int status = Rule.UNREACHABLE

            for (Rule newRule : Model.model.rules) {
                int newStatus = newRule.status(villager)
                if (newStatus < status || (newStatus == status && newRule.rank > rule.rank)) {
                    status = newStatus
                    rule = newRule
                }
            }
            if (rule) {
                rule.startWork(villager, status)
                villager.lookingForWork = false
            }
        }
    }
}
