package main.role.tribe

import main.Main
import main.Model
import main.model.Villager
import main.role.Tribe
import main.role.alone.AloneRole
import main.things.Drawable.SHAPE

import java.awt.Color

class NomadTribe extends Tribe {
    Villager shaman
    List<Villager> followers = []

    NomadTribe() {
        shapeMap[SHAPE.SHAMAN] = [image:null]
        shapeMap[SHAPE.SHAMAN_CAMP] = [image:null]
        shapeMap[SHAPE.FOLLOWER] = [image:null]
    }

    static void work() {
        def tileNetwork = Model.tileNetwork

        for (int i = 0; i < Model.villagers.size(); i++) {
            def aloneMe = Model.villagers[i]

            if (aloneMe.role.id == AloneRole.ID) {
                def (int tileX, int tileY) = aloneMe.getTileXY()

                List<Villager> villagers = []

                Model.getTilesWithinRadii(tileX, tileY, Main.COMFORT_ZONE_TILES) { int x, int y ->
                    tileNetwork[x][y].villagers.each { Villager villager ->
                        if (villager.id != aloneMe.id) {
                            villagers << villager
                        }
                    }
                }

                if (villagers) {
                    def nomadTribe = villagers.role.tribe.find { it instanceof NomadTribe } as NomadTribe

                    if (nomadTribe) {
                        becomeFollower(aloneMe, nomadTribe)
                    } else {
                        Random rand = new Random()

                        NomadTribe myNomadTribe = new NomadTribe()
                        myNomadTribe.shaman = aloneMe
                        myNomadTribe.color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat())

                        aloneMe.role = new ShamanRole(myNomadTribe)
                        aloneMe.setShape(SHAPE.SHAMAN, myNomadTribe)
                        villagers.findAll { it.role.id == AloneRole.ID }.each { def aloneVillager ->
                            becomeFollower(aloneVillager, myNomadTribe)
                        }
                    }
                }
            }
        }
    }

    static void becomeFollower (Villager villager, NomadTribe tribe) {
        villager.role = new FollowerRole(tribe)
        villager.setShape(SHAPE.FOLLOWER, tribe)
        tribe.followers << villager
    }
}
