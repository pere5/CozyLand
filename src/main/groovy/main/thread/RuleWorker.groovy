package main.thread

import main.Model
import main.model.Tile
import main.model.Villager
import main.role.tribe.NomadTribe
import main.rule.Rule

class RuleWorker extends Worker {

    @Override
    def run() {
        super.intendedFps = 3
        super.run()
    }

    def update() {

        placeVillagersInTileNetwork()

        NomadTribe.work()

        assignRules()
    }

    private void placeVillagersInTileNetwork() {
        def tileNetwork = Model.tileNetwork as Tile[][]

        def first = globalWorkCounter == 0

        if (first) {
            for (Villager villager : Model.villagers) {
                def (int villagerX, int villagerY) = villager.getTileXY()
                tileNetwork[villagerX][villagerY].villagers << villager
            }
        } else {
            for (int x = 0; x < tileNetwork.length; x++) {
                for (int y = 0; y < tileNetwork[x].length; y++) {
                    def tile = tileNetwork[x][y]
                    for (int i = tile.villagers.size() - 1; i >= 0; i--) {
                        def villager = tile.villagers[i]
                        def (int tileX, int tileY) = villager.getTileXY()
                        if (tileX != x || tileY != y) {
                            tile.villagers.remove(villager)
                            tileNetwork[tileX][tileY].villagers << villager
                        }
                    }
                }
            }
        }
    }

    private void assignRules() {
        for (Villager villager : Model.villagers) {
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
                rule.toNewState(villager)
            }
        }
    }
}
