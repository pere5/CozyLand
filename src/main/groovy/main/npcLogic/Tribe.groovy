package main.npcLogic

import main.model.Location
import main.things.Drawable
import main.things.naturalResource.NaturalResource
import main.things.resource.Resource

import java.awt.*
import java.awt.image.BufferedImage
import java.util.List
import java.util.concurrent.ConcurrentLinkedQueue

abstract class Tribe {
    Color color
    Map<Drawable.Shape, Map<String, BufferedImage>> shapeMap = [:]
    Map<List<Integer>, Set<NaturalResource>> surveyNaturalResources = [:]
    Location goodLocation
    ConcurrentLinkedQueue<Resource> resources = []
}