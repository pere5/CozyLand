package main.role

import main.things.resource.Resource

import java.awt.Color

abstract class Tribe {
    Color color
    Map shapeMap = [:]
    Map<List<Integer>, Set<Resource>> surveyResources = [:]
    Map<String, Object> goodLocation
}