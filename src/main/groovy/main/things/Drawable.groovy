package main.things

import main.Main
import main.Model
import main.model.Villager
import main.things.naturalResource.NaturalResource
import main.utility.ImageUtils
import main.utility.Utility

import java.awt.*
import java.awt.image.BufferedImage

abstract class Drawable implements Comparable {

    int id
    int parent
    Color color
    Color testColor
    Model.Shape shape = Model.Shape.RECT
    BufferedImage image
    int size = 10
    Double x = 0
    Double y = 0
    Integer offsetX
    Integer offsetY
    Boolean debug = false
    Map<Model.Shape, BufferedImage> shapeImageMap = [:]

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

    void setShapeAndImage(Model.Shape shape) {
        BufferedImage image
        def bufferedImage = Model.shapeImageMap[shape]
        if (bufferedImage) {
            def rnd = new Random().nextInt(bufferedImage.size())
            if (this instanceof Villager) {
                def villager = this as Villager
                if (villager.role.tribe.color) {
                    def tribeImage = villager.role.tribe.shapeImageMap[shape]
                    if (tribeImage) {
                        image = tribeImage
                    } else {
                        image = ImageUtils.applyColorFilter(
                                bufferedImage[rnd],
                                villager.role.tribe.color
                        )
                        villager.role.tribe.shapeImageMap[shape] = image
                    }
                } else if (villager.shapeImageMap[shape]) {
                    image = villager.shapeImageMap[shape]
                } else {
                    image = bufferedImage[rnd]
                    villager.shapeImageMap[shape] = image
                }
            } else if (this instanceof NaturalResource) {
                def travelTypeModel = Model.travelTypeNaturalResources.values().find {
                    it.shape == shape
                }
                def shade = travelTypeModel.shade as Float
                if (shade) {
                    image = ImageUtils.shadeImage(bufferedImage[rnd], shade)
                }
            }

            if (image == null) {
                image = bufferedImage[rnd]
            }
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

    @Override
    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof Drawable)) return false

        Drawable drawable = (Drawable) o

        if (id != drawable.id) return false

        return true
    }

    @Override
    int hashCode() {
        return id
    }

    @Override
    int compareTo(Object o) {
        def a = this
        def b = o as Drawable
        return a.y <=> b.y
    }
}
