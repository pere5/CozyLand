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
        shapeMap[Shape.FOLLOWER_BUILDER] = [image:null]
    }
}
