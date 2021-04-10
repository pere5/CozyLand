package main.things.naturalResource

import main.Model
import main.model.Tile

class Rock extends NaturalResource {

    Rock(Tile tile, Model.Shape shape) {
        super(tile, shape)
    }

    @Override
    public String toString() {
        return "Rock {" +
                "id=" + id +
                '}';
    }
}
