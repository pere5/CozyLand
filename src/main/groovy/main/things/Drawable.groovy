package main.things

import main.Main
import main.Model
import main.model.Villager
import main.utility.ImageUtils
import main.utility.Utility

import java.awt.*
import java.awt.image.BufferedImage

abstract class Drawable {

    enum Shape {
        RECT, CIRCLE, LINE,
        TREE, STONE, WOOD, ROCK,
        SHAMAN, SHAMAN_CAMP, SHAMAN_BUILD, WARRIOR, FOLLOWER, FOLLOWER_BUILDER,
        HUT, SHAMAN_LODGE, GRANARY
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
    Integer offsetX
    Integer offsetY
    Boolean debug = false

    Drawable() {
        this.id = Model.getNewId()
        Model.drawables << this
    }

    Drawable(Boolean addToDrawables) {
        this.id = Model.getNewId()
        if (addToDrawables) {
            Model.drawables << this
        }
    }

    void setShape(Shape shape) {
        BufferedImage image

        if (this instanceof Villager) {
            def villager = this as Villager
            if (villager.role.tribe.color) {
                def shapeMap = villager.role.tribe.shapeMap[shape]
                if (shapeMap.image) {
                    image = shapeMap.image
                } else {
                    image = ImageUtils.applyColorFilter(
                            Model.shapeProperties[shape],
                            villager.role.tribe.color
                    )
                    shapeMap.image = image
                }
            }
        }

        if (image == null) {
            image = Model.shapeProperties[shape]
        }

        this.shape = shape

        if (image) {
            this.image = image

            this.offsetX = (Math.ceil(Main.TILE_WIDTH / 2) - Math.floor(image.width / 2)) as Integer
            this.offsetY = (Math.ceil(Main.TILE_WIDTH / 2) - (image.height)) as Integer
        }
    }

    int[] getTileXY() {
        Utility.pixelToTileIdx(x, y)
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
