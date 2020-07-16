package main.role

import main.Main
import main.Model
import main.model.Villager
import main.rule.Rule
import main.rule.ShamanWalkRule
import main.things.Drawable

import java.awt.Color

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
                    def otherChief = villagers.find { it.role.id == ID } ?: villagers.find { it.role.chief?.role?.id == ID }?.role?.chief

                    if (otherChief) {
                        becomeFollower(me, otherChief)
                    } else {
                        Random rand = new Random()
                        me.role = new ShamanRole()
                        me.shape = Drawable.SHAPE.SHAMAN
                        me.role.tribeColor = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat())

                        //todo: tribe keep color when switching image, e.g fireplace
                        //todo: denna funktion körs nog på tok för ofta tror jag. KOLLA!

                        me.image = Model.applyColorFilter(me.image, me.role.tribeColor)
                        villagers.each { def villager ->
                            becomeFollower(villager, me)
                        }
                    }
                }
            }
        }
    }

    static void becomeFollower (Villager villager, Villager chief) {
        villager.role = new FollowerRole(chief)
        villager.shape = Drawable.SHAPE.FOLLOWER
        villager.image = Model.applyColorFilter(villager.image, chief.role.tribeColor)
        chief.role.followers << villager
    }
}
