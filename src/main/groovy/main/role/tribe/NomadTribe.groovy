package main.role.tribe

import main.Main
import main.Model
import main.model.Villager
import main.role.Tribe
import main.role.alone.AloneRole
import main.things.Drawable

import java.awt.Color
import java.awt.image.BufferedImage

class NomadTribe extends Tribe {
    Villager shaman
    List<Villager> followers = []
    BufferedImage followerImage
    BufferedImage shamanImage

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
                        aloneMe.setShape(Drawable.SHAPE.SHAMAN, myNomadTribe)
                        villagers.findAll { it.role.id == AloneRole.ID }.each { def aloneVillager ->
                            becomeFollower(aloneVillager, myNomadTribe)
                        }
                        //todo: tribe keep color when switching image, e.g fireplace
                        //todo: denna funktion körs nog på tok för ofta tror jag. KOLLA!
                    }
                }
            }
        }
    }

    static void becomeFollower (Villager villager, NomadTribe tribe) {
        villager.role = new FollowerRole(tribe)
        villager.setShape(Drawable.SHAPE.FOLLOWER, tribe)
        tribe.followers << villager
    }
}
