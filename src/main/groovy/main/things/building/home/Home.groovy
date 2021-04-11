package main.things.building.home

import main.Model
import main.model.Villager
import main.things.building.Building

abstract class Home extends Building {

    List<Villager> habitants = []
    Integer maxHabitants

    Home(Model.Shape shape, Integer maxHabitants, Villager me) {
        super(shape, me.tileXY)
        this.maxHabitants = maxHabitants
        habitants << me
        me.home = this
        me.role.tribe.buildings << this
    }
}
