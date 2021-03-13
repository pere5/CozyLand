package main.things.building.home

import main.model.Villager
import main.things.building.Building

abstract class Home extends Building {

    List<Villager> habitants = []
    Integer maxHabitants

    Home(Shape shape, Integer maxHabitants) {
        super(shape)
        this.maxHabitants = maxHabitants
    }
}
