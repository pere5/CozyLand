package main.model


import main.Model
import main.things.Drawable
import main.things.naturalResource.NaturalResource

import java.util.concurrent.ConcurrentLinkedQueue

class Tile extends Drawable {

    Tile() {
        super(false)
    }

    List<NaturalResource> naturalResources = []
    int height = 0
    Model.TravelType travelType
    ConcurrentLinkedQueue<Villager> villagers = []
}
