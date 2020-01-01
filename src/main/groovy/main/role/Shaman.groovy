package main.role

import main.Model
import main.model.Villager
import main.things.Drawable
import main.thread.RuleWorker

class Shaman extends Role {

    static final String ID = 'shaman'

    Shaman () {
        super.id = ID
        super.rules.addAll(RuleWorker.shamanRules())
    }

    static void assignShamans() {
        for (int i = 0; i < Model.villagers.size(); i++) {
            def me = Model.villagers[i]

            if (me.role.id == Base.ID) {
                def (int villagerX, int villagerY) = me.getTileXY()

                List<Villager> villagers = []

                Model.getPointsWithinRadii(villagerX, villagerY, Villager.COMFORT_ZONE_TILES) { int x, int y ->
                    Model.tileNetwork[x][y].villagers.each { Villager villager ->
                        if (villager.id != me.id) {
                            villagers << villager
                        }
                    }
                }

                if (villagers) {
                    def boss = villagers.find { it.role.id != Base.ID } ?: villagers.find { it.role.boss != null }?.role?.boss

                    if (boss) {
                        me.role = new Follower(boss)
                        me.rules.addAll(me.role.rules)
                        me.shape = Drawable.SHAPES.FOLLOWER
                        me.image = Model.followerImage
                        boss.role.villagers << me
                    } else {
                        me.role = new Shaman()
                        me.rules.addAll(me.role.rules)
                        me.shape = Drawable.SHAPES.SHAMAN
                        me.image = Model.shamanImage
                    }
                }
            }
        }
    }
}
