package main.role

import main.Main
import main.Model
import main.model.Villager
import main.rule.Rule
import main.rule.ShamanWalk
import main.things.Drawable

class Shaman extends Role {

    static final String ID = 'shaman'

    static List<Rule> getRules() {
        int rank = Integer.MAX_VALUE
        [
                new ShamanWalk(rank: --rank)
        ]
    }

    Shaman () {
        super.id = ID
        super.rules = getRules()
    }

    static void assignShamans() {
        def tileNetwork = Model.tileNetwork

        for (int i = 0; i < Model.villagers.size(); i++) {
            def me = Model.villagers[i]

            if (me.role.id == Base.ID) {
                def (int tileX, int tileY) = me.getTileXY()

                List<Villager> villagers = []

                Model.getTilesWithinRadii(tileX, tileY, Main.COMFORT_ZONE_TILES) { int x, int y ->
                    tileNetwork[x][y].villagers.each { Villager villager ->
                        if (villager.id != me.id) {
                            villagers << villager
                        }
                    }
                }

                if (villagers) {
                    def boss = villagers.find { it.role.id == ID } ?: villagers.find { it.role.boss?.role?.id == ID }?.role?.boss

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
