package main


import main.person.Person
import main.input.MyKeyboardListener
import main.input.MyMouseListener
import main.things.Stone
import main.things.Tree

import javax.swing.*
import java.awt.*
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

class Main extends JFrame {

    static int WINDOW_WIDTH = 1000
    static int WINDOW_HEIGHT = 750

    static int idGenerator = 0
    MyKeyboardListener myKeyboardListener
    MyMouseListener myMouseListener

    Main() {
        initUI()
    }

    static int getNewId() {
        idGenerator++
    }

    def initUI() {

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

        def model = [
                pause: false,
                drawables: drawables
        ]
        
        def surface = new Surface(model: model)

        myMouseListener = new MyMouseListener()
        surface.addMouseListener(myMouseListener)
        myKeyboardListener = new MyKeyboardListener(model: model)
        addKeyListener(myKeyboardListener)

        Timer timer = new Timer(15, surface)
        timer.start()

        Thread.start {
            new ThreadWorker(model: model, index: 0).run()
        }
        Thread.start {
            new ThreadWorker(model: model, index: 1).run()
        }
        Thread.start {
            new ThreadWorker(model: model, index: 2).run()
        }
        Thread.start {
            new ThreadWorker(model: model, index: 3).run()
        }
        Thread.start {
            new ThreadWorker(model: model, index: 4).run()
        }

        add(surface)

        addWindowListener(new WindowAdapter() {
            @Override
            void windowClosing(WindowEvent e) {
                timer.stop()
            }
        })

        setTitle("CozyLand")
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT)
        setLocationRelativeTo(null)
        setDefaultCloseOperation(EXIT_ON_CLOSE)
        setVisible(true)
    }

    static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            @Override
            void run() {
                new Main()
            }
        })
    }


}