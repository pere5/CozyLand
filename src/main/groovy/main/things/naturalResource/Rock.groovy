package main.things.naturalResource

import main.Model
import main.model.Tile

class Rock extends NaturalResource {

    Rock(Tile tile) {
        super(tile, Model.Shape.ROCK)
    }

    @Override
    public String toString() {
        return "Rock {" +
                "id=" + id +
                '}';
    }
}
