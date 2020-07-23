package main.things

import main.Model
import main.role.Tribe

import java.awt.*
import java.awt.image.BufferedImage

abstract class Drawable {

    enum Shape {
        RECT, CIRCLE,
        TREE, STONE, WOOD, ROCK,
        SHAMAN, SHAMAN_CAMP, SHAMAN_BUILD, WARRIOR, FOLLOWER, FOLLOWER_BUILDER,
        HUT, SHAMAN_HUT, GRANARY
    }

    int id
    int parent
    Color color
    Color testColor
    Shape shape = Shape.RECT
    BufferedImage image
    int size = 10
    Double x = 0
    Double y = 0

    Drawable() {
        this.id = Model.getNewId()
    }

    void setShape(Shape shape) {
        setShape(shape, null)
    }

    void setShape(Shape shape, Tribe tribe) {
        BufferedImage image

        def shapeMap = tribe?.shapeMap?.get(shape)
        if (shapeMap) {
            if (shapeMap.image) {
                image = shapeMap.image
            } else {
                image = Model.applyColorFilter(
                        Model.shapeProperties[shape].image as BufferedImage,
                        tribe.color
                )
                shapeMap.image = image
            }
        }

        if (image == null) {
            image = Model.shapeProperties[shape].image as BufferedImage
        }

        this.shape = shape
        this.image = image
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
