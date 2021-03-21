package main.npcLogic

import main.Model
import main.model.Location
import main.model.Villager
import main.things.Drawable
import main.things.naturalResource.NaturalResource
import main.things.resource.Resource

import java.awt.*
import java.awt.image.BufferedImage
import java.util.List
import java.util.concurrent.ConcurrentLinkedQueue

abstract class Tribe {

    int id

    Villager ruler
    ConcurrentLinkedQueue<Villager> villagers = []

    Color color
    Map<Drawable.Shape, BufferedImage> shapeImageMap = [:]
    ConcurrentLinkedQueue<Resource> resources = []
    Map<List<Integer>, Set<NaturalResource>> surveyNaturalResources = [:]
    Location location

    Tribe () {
        this.id = Model.getNewId()
    }

    abstract Role getNewRulerRole()
    abstract Role getNewVillagerRole()
}