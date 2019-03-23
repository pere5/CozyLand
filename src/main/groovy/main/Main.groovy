package main


import main.person.Person
import main.input.MyKeyboardListener
import main.input.MyMouseListener
import main.things.Stone
import main.things.Tree

import javax.swing.*
import java.awt.Dimension
import java.awt.EventQueue
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

class Main extends JFrame {


    MyKeyboardListener myKeyboardListener
    MyMouseListener myMouseListener

    Main() {
        super('CozyLand')

        setTitle("CozyLand")
        setSize(Model.WINDOW_WIDTH, Model.WINDOW_HEIGHT)
        setLocationRelativeTo(null)
        setDefaultCloseOperation(EXIT_ON_CLOSE)

        Model.init()

        def surface = new Surface()

        myMouseListener = new MyMouseListener()
        surface.addMouseListener(myMouseListener)
        myKeyboardListener = new MyKeyboardListener()
        addKeyListener(myKeyboardListener)


        def background = new Background()

        def layeredPane = getLayeredPane()
        background.setOpaque(false)
        surface.setOpaque(false)
        background.setBounds(200, 200, 100, 100);
        surface.setBounds(10, 10, 500, 500);
        layeredPane.add(surface, 1)
        layeredPane.add(background, 2)

        //add background
        //add surface

        Timer timer = new Timer(15, surface)
        timer.start()
        Timer timer2 = new Timer(1100, background)
        timer2.start()

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

        addWindowListener(new WindowAdapter() {
            @Override
            void windowClosing(WindowEvent e) {
                timer.stop()
            }
        })

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