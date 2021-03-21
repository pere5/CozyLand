package main.utility

import main.model.Villager
import main.npcLogic.Tribe

import java.awt.*

class GameUtils {

    static void joinTribe(Villager villager, Tribe newTribe) {
        villager.role = newTribe.getNewVillagerRole()
        newTribe.villagers << villager
        villager.interrupt()
    }

    static void transformTribe(Tribe oldTribe, Tribe newTribe) {
        Random rand = new Random()
        def ruler = oldTribe.ruler

        newTribe.ruler = ruler
        newTribe.color = oldTribe.color ?: new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat())
        newTribe.surveyNaturalResources = oldTribe.surveyNaturalResources
        newTribe.resources = oldTribe.resources
        newTribe.shapeImageMap = oldTribe.shapeImageMap
        newTribe.location = oldTribe.location

        ruler.role = newTribe.getNewRulerRole()
        ruler.interrupt()
        oldTribe.villagers.each { Villager villager ->
            joinTribe(villager, newTribe)
        }
    }
}
