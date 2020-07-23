package main.role

import main.things.naturalResource.NaturalResource
import main.things.resource.Resource

import java.awt.*
import java.util.List

abstract class Tribe {
    Color color
    Map shapeMap = [:]
    Map<List<Integer>, Set<NaturalResource>> surveyNaturalResources = [:]
    Map<String, Object> goodLocation
    Set<Resource> resources = []
}