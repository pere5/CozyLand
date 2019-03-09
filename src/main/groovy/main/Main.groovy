package main

import javaSrc.ExampleFactory
import javaSrc.ExampleNode
import javaSrc.Map
import main.aStar.StarNode
import main.aStar.StarNodeFactory
import main.person.Person
import main.input.MyKeyboardListener
import main.input.MyMouseListener
import main.things.Stone
import main.things.Tree

import javax.swing.*
import java.awt.EventQueue
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
        
        def surface = new Surface()

        myMouseListener = new MyMouseListener()
        surface.addMouseListener(myMouseListener)
        myKeyboardListener = new MyKeyboardListener()
        addKeyListener(myKeyboardListener)

        Timer timer = new Timer(15, surface)
        timer.start()

        Thread.start {
            new ThreadWorker(index: 0).run()
        }
        Thread.start {
            new ThreadWorker(index: 1).run()
        }
        Thread.start {
            new ThreadWorker(index: 2).run()
        }
        Thread.start {
            new ThreadWorker(index: 3).run()
        }
        Thread.start {
            new ThreadWorker(index: 4).run()
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