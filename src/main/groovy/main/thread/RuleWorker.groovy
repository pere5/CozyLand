package main.thread

import main.Model
import main.model.Tile
import main.model.Villager
import main.role.Shaman
import main.rule.Affinity
import main.rule.Rule
import main.rule.shaman.Migrate
import main.rule.shaman.VillageSearch

class RuleWorker extends Worker {

    @Override
    def run() {
        super.intendedFps = 3
        super.run()
    }

    static List<Rule> aliveRules() {
        int rank = Integer.MAX_VALUE - 300
        [
                new Affinity(rank: --rank)
                //new RandomBigWalk(rank: --rank)
        ]
    }

    static List<Rule> shamanRules() {
        int rank = Integer.MAX_VALUE - 100
        [
                new VillageSearch(rank: --rank)
        ]
    }

    static List<Rule> shamanSubjectRules() {
        int rank = Integer.MAX_VALUE - 200
        [
                new Migrate(rank: --rank)
        ]
    }

    def update() {

        prepareVillagers()

        Shaman.assignShamans()

        assignRules()
    }

    private void prepareVillagers() {
        def tileNetwork = Model.tileNetwork as Tile[][]

        if (counter == 0) {
            for (Villager villager : Model.villagers) {
                int[] tileXY = villager.getTile()
                tileNetwork[tileXY[0]][tileXY[1]].villagers << villager
            }
        } else {
            for (int x = 0; x < tileNetwork.length; x++) {
                for (int y = 0; y < tileNetwork[x].length; y++) {
                    def tile = tileNetwork[x][y]
                    for (int i = tile.villagers.size() - 1; i >= 0; i--) {
                        def villager = tile.villagers[i]
                        int[] tileXY = villager.getTile()
                        if (tileXY[0] != x || tileXY[1] != y) {
                            tile.villagers.remove(villager)
                            tileNetwork[tileXY[0]][tileXY[1]].villagers << villager
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
