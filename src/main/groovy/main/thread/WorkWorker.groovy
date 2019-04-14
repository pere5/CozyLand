package main.thread

import main.Model
import main.person.Person

class WorkWorker extends Worker {

    int frameIndex = 1

    def update() {
        (Model.model.persons as List<Person>)*.work()
        def frameSlots = Model.model.frameSlots as List
        frameSlots[frameIndex] = lastFramesPerSecond
    }
}
