package main.model

import main.things.naturalResource.NaturalResource

class Location {
    int[] tileXY
    Set<NaturalResource> naturalResources
    Integer score
}
