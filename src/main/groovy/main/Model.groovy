package main


import javaSrc.Map
import main.aStar.StarNodeFactory
import main.aStar.StarNode
import main.person.Person
import main.things.Stone
import main.things.Tree

class Model {

    static model

    static {
        def persons = [
                new Person(), new Person(), new Person(), new Person(), new Person()
        ]
        def stones = [
                new Stone(), new Stone(), new Stone(), new Stone(), new Stone(),
                new Stone(), new Stone(), new Stone(), new Stone(), new Stone(),
                new Stone(), new Stone(), new Stone(), new Stone(), new Stone(),
                new Stone(), new Stone(), new Stone(), new Stone(), new Stone(),
                new Stone(), new Stone(), new Stone(), new Stone(), new Stone(),
                new Stone(), new Stone(), new Stone(), new Stone(), new Stone()
        ]
        def trees = [
                new Tree(), new Tree(), new Tree(), new Tree(), new Tree(),
                new Tree(), new Tree(), new Tree(), new Tree(), new Tree(),
                new Tree(), new Tree(), new Tree(), new Tree(), new Tree(),
                new Tree(), new Tree(), new Tree(), new Tree(), new Tree(),
                new Tree(), new Tree(), new Tree(), new Tree(), new Tree(),
                new Tree(), new Tree(), new Tree(), new Tree(), new Tree()
        ]

        def drawables = [
                persons, stones, trees
        ].flatten()

        Map<StarNode> myMap = new Map<StarNode>(50, 50, new StarNodeFactory())

        def model = [
                pause: false,
                drawables: drawables,
                myMap: myMap
        ]
        this.model = model
    }
}
