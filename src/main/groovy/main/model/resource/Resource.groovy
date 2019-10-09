package main.model.resource

import main.Model
import main.things.Drawable

abstract class Resource extends Drawable {

    int id

    Resource () {
        this.id = Model.getNewId()
    }


    blergh
}
