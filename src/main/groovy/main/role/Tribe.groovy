package main.role

import main.model.Location
import main.things.naturalResource.NaturalResource
import main.things.resource.Resource

import java.awt.*
import java.util.List

abstract class Tribe {
    Color color
    Map shapeMap = [:]
    Map<List<Integer>, Set<NaturalResource>> surveyNaturalResources = [:]
    Location goodLocation
    Set<Resource> resources = []
}