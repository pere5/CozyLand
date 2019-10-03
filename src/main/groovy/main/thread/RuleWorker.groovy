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

    static List<Rule> baseRules() {
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

        placeVillagersInTileNetwork()

        Shaman.assignShamans()

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
                        def (int villagerX, int villagerY) = villager.getTileXY()
                        if (villagerX != x || villagerY != y) {
                            tile.villagers.remove(villager)
                            tileNetwork[villagerX][villagerY].villagers << villager
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
