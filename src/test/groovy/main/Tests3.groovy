package main

import main.model.Tile
import main.utility.BackgroundUtils
import org.junit.Test

class Tests3 {
    @Test
    void background() {
        Tile[][] tileNetwork = BackgroundUtils.generateBackground("maps/lol${(Math.random() * 4 + 1) as int}.png", true)
    }
}
