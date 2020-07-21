package main.role

import main.things.resource.Resource

import java.awt.Color

abstract class Tribe {
    Color color
    Map shapeMap = [:]
    Map<int[], Set<Resource>> surveyResources = [:]
}