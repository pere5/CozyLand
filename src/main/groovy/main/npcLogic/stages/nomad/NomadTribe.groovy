package main.npcLogic.stages.nomad


import main.npcLogic.Tribe
import main.things.Drawable.Shape

class NomadTribe extends Tribe {

    NomadTribe() {
        shapeMap[Shape.SHAMAN] = [image:null]
        shapeMap[Shape.SHAMAN_CAMP] = [image:null]
        shapeMap[Shape.SHAMAN_BUILD] = [image:null]
        shapeMap[Shape.FOLLOWER] = [image:null]
        shapeMap[Shape.FOLLOWER_BUILDER] = [image:null]
    }
}
