package main.thread

import main.Model
import main.person.Person

class WorkWorker extends Worker {

    def update() {
        (Model.model.persons as List<Person>)*.work()
    }
}
