package main.thread

import main.Model
import main.model.Tile
import main.rule.Rule
import main.rule.alive.Alive
import main.rule.shaman.Shaman
import main.villager.Villager

import java.awt.*
import java.util.List

class RuleWorker extends Worker {

    @Override
    def run() {
        super.intendedFps = 3
        super.run()
    }

    def update() {

        prepareVillagers()

        assignShamans()

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

    private void assignShamans() {

        /*
            Optimization: Use a dude buffer
            private static final List<Villager> dudes = new ArrayList<>(Model.villagers.size())
        */

        /*
            Okej
            trädstruktur med ledarskapsnivåer
            en ledare lägger in rules i sin undersåters privata ruleLists

             - [ ] Man måste tracka villagerna status för ress olika roller.
             - [ ] Assigna roller baserat på villagers status i dess rules.
         */

        for (int i = 0; i < Model.villagers.size(); i++) {
            def me = Model.villagers[i]

            if (me.boss == null && me.role.id == Alive.ID) {
                def (int tX, int tY) = me.getTile()

                List<Villager> dudes = []

                Model.calculateWithinRadii(tY, tX, Villager.COMFORT_ZONE_TILES) { int x, int y ->
                    Model.tileNetwork[x][y].villagers.each { Villager dude ->
                        if (dude.id != me.id) {
                            dudes << dude
                        }
                    }
                }

                if (dudes) {
                    def noBossAround = dudes.size() == dudes.count { it.boss == null && me.role.id == Alive.ID }

                    if (noBossAround) {
                        me.role = new Shaman()
                        me.role.villages.addAll(dudes)
                        me.color = Color.GREEN
                        dudes.each {
                            it.boss = me
                            it.color = Color.LIGHT_GRAY
                            it.rules.addAll(me.role.rules)
                        }
                    } else {

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
