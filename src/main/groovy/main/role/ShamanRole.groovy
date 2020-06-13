package main.role

import main.Main
import main.Model
import main.model.Villager
import main.rule.Rule
import main.rule.ShamanWalkRule
import main.things.Drawable

import java.awt.image.BufferedImage

class ShamanRole extends Role {

    static final String ID = 'shaman'

    static List<Rule> getRules() {
        int rank = Integer.MAX_VALUE
        [
                new ShamanWalkRule(rank: --rank)
        ]
    }

    ShamanRole() {
        super.id = ID
        super.rules = getRules()
    }

    static void assignShamans() {
        def tileNetwork = Model.tileNetwork

        for (int i = 0; i < Model.villagers.size(); i++) {
            def me = Model.villagers[i]

            if (me.role.id == BaseRole.ID) {
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
                        me.role = new FollowerRole(boss)
                        me.shape = Drawable.SHAPE.FOLLOWER
                        me.image = Model.shapeProperties[me.shape].image as BufferedImage
                        boss.role.villagers << me
                    } else {
                        me.role = new ShamanRole()
                        me.shape = Drawable.SHAPE.SHAMAN
                        me.image = Model.shapeProperties[me.shape].image as BufferedImage
                    }
                }
            }
        }
    }
}
