package main.role

import main.Model
import main.model.Villager
import main.rule.Rule
import main.rule.shaman.VillageSearch
import main.things.Drawable

class Shaman extends Role {

    static final String ID = 'shaman'

    static List<Rule> shamanRules() {
        int rank = Integer.MAX_VALUE
        [
                new VillageSearch(rank: --rank)
        ]
    }

    Shaman () {
        super.id = ID
        super.rules = shamanRules()
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
                        me.shape = Drawable.SHAPES.FOLLOWER
                        me.image = Model.followerImage
                        boss.role.villagers << me
                    } else {
                        me.role = new Shaman()
                        me.shape = Drawable.SHAPES.SHAMAN
                        me.image = Model.shamanImage
                    }
                }
            }
        }
    }
}
