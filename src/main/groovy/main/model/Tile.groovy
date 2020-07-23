package main.model

import main.Main
import main.Model
import main.things.Drawable
import main.things.naturalResource.NaturalResource

class Tile extends Drawable {

    static Tile w(int x, int y) {
        new Tile(height: 10, size: Main.TILE_WIDTH, x: x, y: y, travelType: Model.TravelType.WATER)
    }

    static Tile p(int x, int y) {
        new Tile(height: 10, size: Main.TILE_WIDTH, x: x, y: y, travelType: Model.TravelType.PLAIN)
    }

    List<NaturalResource> naturalResources = []
    int height = 0
    Model.TravelType travelType
    List<Villager> villagers = []
}
