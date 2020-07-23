package main.role.tribe

import main.Main
import main.Model
import main.model.Villager
import main.role.Tribe
import main.role.alone.AloneRole
import main.things.Drawable.Shape

import java.awt.*
import java.util.List
import java.util.concurrent.ConcurrentLinkedQueue

class NomadTribe extends Tribe {
    Villager shaman
    ConcurrentLinkedQueue<Villager> followers = []

    NomadTribe() {
        shapeMap[Shape.SHAMAN] = [image:null]
        shapeMap[Shape.SHAMAN_CAMP] = [image:null]
        shapeMap[Shape.SHAMAN_BUILD] = [image:null]
        shapeMap[Shape.FOLLOWER] = [image:null]
    }

    static void work(Villager villager) {
        if (villager.role.id == AloneRole.ID) {
            def aloneMe = villager
            def tileNetwork = Model.tileNetwork
            def (int tileX, int tileY) = aloneMe.getTileXY()

            List<Villager> neighbors = []

            Model.getTilesWithinRadii(tileX, tileY, Main.COMFORT_ZONE_TILES) { int x, int y ->
                tileNetwork[x][y].villagers.each { Villager neighbor ->
                    if (neighbor.id != aloneMe.id) {
                        neighbors << neighbor
                    }
                }
            }

            if (neighbors) {
                def nomadTribe = neighbors.role.tribe.find { it instanceof NomadTribe } as NomadTribe

                if (nomadTribe) {
                    becomeFollower(aloneMe, nomadTribe)
                } else {
                    Random rand = new Random()

                    NomadTribe myNomadTribe = new NomadTribe()
                    myNomadTribe.shaman = aloneMe
                    myNomadTribe.color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat())

                    aloneMe.role = new ShamanRole(myNomadTribe)
                    aloneMe.setShape(Shape.SHAMAN, myNomadTribe)
                    neighbors.findAll { it.role.id == AloneRole.ID }.each { def aloneVillager ->
                        becomeFollower(aloneVillager, myNomadTribe)
                    }
                }
            }
        }
    }

    static void becomeFollower (Villager villager, NomadTribe tribe) {
        villager.role = new FollowerRole(tribe)
        villager.setShape(Shape.FOLLOWER, tribe)
        tribe.followers << villager
    }
}
