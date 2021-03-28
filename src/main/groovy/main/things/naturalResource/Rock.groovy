package main.things.naturalResource


import main.model.Tile

class Rock extends NaturalResource {

    Rock(Tile tile) {
        super(tile, Shape.SILVER_ORDE)
    }

    @Override
    public String toString() {
        return "Rock {" +
                "id=" + id +
                '}';
    }
}
