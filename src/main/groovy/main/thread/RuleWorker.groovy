package main.thread

import main.Model
import main.model.Villager
import main.npcLogic.Rule

class RuleWorker extends Worker {

    @Override
    def run() {
        super.intendedFps = 3
        super.run()
    }

    def update() {
        for (Villager villager : Model.villagers) {
            assignRules(villager)
        }
    }

    private static void assignRules(Villager villager) {
        if (villager.ruleWorker) {
            def rule = null
            int status = Rule.UNREACHABLE

            for (Rule newRule : villager.role.rules) {
                int newStatus = newRule.status(villager)
                if ((newStatus < status) || (rule ? (newStatus == status && newRule.rank > rule.rank) : true)) {
                    status = newStatus
                    rule = newRule
                }
            }

            rule.planWork(villager, status)
            villager.toWorkWorker()
        }
    }
}
