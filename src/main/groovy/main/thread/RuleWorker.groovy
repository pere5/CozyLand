package main.thread

import main.Model
import main.rule.Rule
import main.villager.Villager

class RuleWorker extends Worker {

    def update() {

        for (Villager villager: Model.villagers) {
            if (villager.ruleWorker) {
                def rule = null
                int status = Rule.UNREACHABLE

                for (Rule newRule : Model.rules) {
                    int newStatus = newRule.status(villager)
                    if ((newStatus < status) || (rule ? (newStatus == status && newRule.rank > rule.rank) : true)) {
                        status = newStatus
                        rule = newRule
                    }
                }

                rule.startWork(villager, status)
                rule.toNewState(villager)
            }
        }
    }
}
