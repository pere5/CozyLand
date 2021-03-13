package main.things.building

import main.Model
import main.model.ResourceSlot
import main.things.Drawable

abstract class Building extends Drawable {

    List<ResourceSlot> resourceSlots = []

    Building(Shape shape) {
        super()
        Model.buildingResources[shape].each { def resource ->
            resource.value.times {
                resourceSlots << new ResourceSlot(shape: resource.key, resource: null)
            }
        }
    }
}
