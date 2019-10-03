package main.role

import main.Model
import main.model.Villager
import main.thread.RuleWorker

import java.awt.*
import java.util.List

class Shaman extends Role {

    static final String ID = 'shaman'

    Shaman () {
        super.id = ID
        super.subjectRules.addAll(RuleWorker.shamanSubjectRules())
        super.rules.addAll(RuleWorker.shamanRules())
    }

    static void assignShamans() {
        for (int i = 0; i < Model.villagers.size(); i++) {
            def me = Model.villagers[i]

            if (me.boss == null && me.role.id == Base.ID) {
                def (int villagerX, int villagerY) = me.getTileXY()

                List<Villager> dudes = []

                Model.getPointsWithinRadii(villagerX, villagerY, Villager.COMFORT_ZONE_TILES) { int x, int y ->
                    Model.tileNetwork[x][y].villagers.each { Villager dude ->
                        if (dude.id != me.id) {
                            dudes << dude
                        }
                    }
                }

                if (dudes) {
                    def boss = dudes.find { it.role.id != Base.ID } ?: dudes.find { it.boss != null }?.boss

                    if (boss) {
                        me.boss = boss
                        me.color = Color.LIGHT_GRAY
                        me.rules.addAll(boss.role.subjectRules)
                        boss.role.villagers << me
                    } else {
                        me.role = new Shaman()
                        me.rules.addAll(me.role.rules)
                        me.color = Color.GREEN
                    }
                }
            }
        }
    }
}
