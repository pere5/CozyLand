package main.thread

import main.Model
import main.model.Tile
import main.rule.Affinity
import main.rule.Rule
import main.villager.Villager

class RuleWorker extends Worker {

    @Override
    def run() {
        super.intendedFps = 3
        super.run()
    }

    static List<Rule> generateStandardRules() {
        int rank = Integer.MAX_VALUE
        [
                //new RandomBigWalk(rank: --rank)
                new Affinity(rank: --rank)
        ]
    }

    def update() {

        def tileNetwork = Model.tileNetwork as Tile[][]

        if (counter == 0) {
            for (Villager villager: Model.villagers) {
                int[] tileXY = villager.getTile()
                tileNetwork[tileXY[0]][tileXY[1]].villagers << villager
            }
        } else if (counter % 3 == 0) {
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

        /*
            Okej
            trädstruktur med ledarskapsnivåer
            en ledare lägger in rules i sin undersåters privata ruleLists

             - [ ] Man måste tracka villagerna status för ress olika roller.
             - [ ] Assigna roller baserat på villagers status i dess rules.
         */

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
