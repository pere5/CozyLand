package main.things.building

import main.Model
import main.model.ResourceSlot
import main.things.Drawable
import main.utility.Utility

abstract class Building extends Drawable {

    List<ResourceSlot> resourceSlots = []

    Building(Model.Shape shape, int[] spot) {
        super()
        def (Double x, Double y) = Utility.randomPlaceInTile(spot)
        this.x = x
        this.y = y
        setShapeAndImage(Model.Shape.HUT)
        Model.buildingResources[shape].each { def resource ->
            resource.value.times {
                resourceSlots << new ResourceSlot(shape: resource.key, resource: null)
            }
        }
    }
}
