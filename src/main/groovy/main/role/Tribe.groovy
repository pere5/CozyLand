package main.role

import main.things.resource.NaturalResource

import java.awt.*
import java.util.List

abstract class Tribe {
    Color color
    Map shapeMap = [:]
    Map<List<Integer>, Set<NaturalResource>> surveyNaturalResources = [:]
    Map<String, Object> goodLocation
    Set<NaturalResource> naturalResources = []
}