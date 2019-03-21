package main


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


    MyKeyboardListener myKeyboardListener
    MyMouseListener myMouseListener

    Main() {
        initUI()
    }

    def initUI() {
        Model.init()

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
        setSize(Model.WINDOW_WIDTH, Model.WINDOW_HEIGHT)
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