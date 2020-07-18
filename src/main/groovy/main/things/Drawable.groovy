package main.things

import main.Model
import main.exception.PerIsBorkenException
import main.role.Tribe
import main.role.tribe.NomadTribe

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

    void setShape(SHAPE shape) {
        setShape(shape, null)
    }

    void setShape(SHAPE shape, Tribe tribe) {
        BufferedImage image

        if (tribe instanceof NomadTribe) {
            if (shape == SHAPE.SHAMAN && tribe.shamanImage) {
                image = tribe.shamanImage
            } else if (shape == SHAPE.SHAMAN_CAMP && tribe.shamanCampImage) {
                image = tribe.shamanCampImage
            } else if (shape == SHAPE.FOLLOWER && tribe.followerImage) {
                image = tribe.followerImage
            }
            if (image == null) {
                image = Model.applyColorFilter(
                        Model.shapeProperties[shape].image as BufferedImage,
                        tribe.color
                )
                if (shape == SHAPE.SHAMAN) {
                    tribe.shamanImage = image
                } else if (shape == SHAPE.SHAMAN_CAMP) {
                    tribe.shamanCampImage = image
                } else if (shape == SHAPE.FOLLOWER) {
                    tribe.followerImage = image
                }
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
