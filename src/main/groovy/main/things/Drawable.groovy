package main.things

import main.Model
import main.role.Tribe

import java.awt.*
import java.awt.image.BufferedImage

abstract class Drawable {

    enum SHAPE {
        RECT, CIRCLE, TREE, STONE, SHAMAN, SHAMAN_CAMP, WARRIOR, FOLLOWER
    }

    int id
    int parent
    Color color
    Color testColor
    SHAPE shape = SHAPE.RECT
    BufferedImage image
    int size = 10
    Double x = 0
    Double y = 0

    Drawable() {
        this.id = Model.getNewId()
    }

    void setShape(SHAPE shape, Tribe tribe) {
        this.shape = shape
        def image = Model.shapeProperties[shape].image as BufferedImage
        this.image = tribe?.color ? Model.applyColorFilter(image, tribe.color) : image
    }

    int[] getTileXY() {
        Model.pixelToTileIdx(x, y)
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof Drawable)) return false

        Drawable drawable = (Drawable) o

        if (id != drawable.id) return false

        return true
    }

    int hashCode() {
        return id
    }


}
