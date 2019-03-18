package main

import main.person.Person
import main.things.Stone
import main.things.Tree

class Model {

    static model

    static def init() {
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

        def background = generateBackground()

        def model = [
                pause: false,
                drawables: drawables,
                background: background
        ]
        this.model = model
    }

    static Object generateBackground() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("Lol_Height_Map_Merged.png");
        int lol = 0
    }
}
