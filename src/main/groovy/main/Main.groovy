package main

import main.drawers.Surface
import main.input.MyKeyboardListener
import main.input.MyMouseListener
import main.thread.ThreadWorker

import javax.swing.*
import java.awt.*
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

class Main extends JFrame {

    static int WINDOW_WIDTH = 1200
    static int WINDOW_HEIGHT = 900

    static int MAP_WIDTH
    static int MAP_HEIGHT

    static int VIEWPORT_WIDTH
    static int VIEWPORT_HEIGHT

    MyKeyboardListener myKeyboardListener
    MyMouseListener myMouseListener

    Main() {
        super('CozyLand')

        pack()
        VIEWPORT_WIDTH = WINDOW_WIDTH - (getWidth() - getContentPane().getWidth())
        VIEWPORT_HEIGHT = WINDOW_HEIGHT - (getHeight() - getContentPane().getHeight())
        MAP_WIDTH = VIEWPORT_WIDTH * 2
        MAP_HEIGHT = VIEWPORT_HEIGHT * 2

        myKeyboardListener = new MyKeyboardListener()
        myMouseListener = new MyMouseListener()
        Model.init(myKeyboardListener, myMouseListener)

        addKeyListener(myKeyboardListener)
        def surface = new Surface()
        surface.addMouseListener(myMouseListener)

        add(surface)

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

        addWindowListener(new WindowAdapter() {
            @Override
            void windowClosing(WindowEvent e) {
                timer.stop()
            }
        })

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