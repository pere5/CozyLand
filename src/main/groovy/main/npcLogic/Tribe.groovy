package main.npcLogic


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

    Villager ruler
    ConcurrentLinkedQueue<Villager> villagers = []

    Color color
    Map<Drawable.Shape, BufferedImage> shapeImageMap = [:]
    ConcurrentLinkedQueue<Resource> resources = []
    Map<List<Integer>, Set<NaturalResource>> surveyNaturalResources = [:]
    Location goodLocation
    Location location

    abstract Role getNewRulerRole()
    abstract Role getNewVillagerRole()
}