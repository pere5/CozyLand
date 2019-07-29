package main.thread

import main.Model
import main.rule.Rule
import main.villager.Villager

class RuleWorker extends Worker {

    @Override
    def run() {
        super.intendedFps = 2
        super.run()
    }

    def update() {

        for (Villager villager: Model.villagers) {
            if (villager.ruleWorker) {
                def rule = null
                int status = Rule.UNREACHABLE

                for (Rule newRule : villager.rules) {
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
