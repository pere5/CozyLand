package main

import main.drawers.Background
import main.drawers.Surface
import main.input.MyKeyboardListener
import main.input.MyMouseListener
import main.thread.ThreadWorker

import javax.swing.*
import java.awt.EventQueue
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

class Main extends JFrame {


    MyKeyboardListener myKeyboardListener
    MyMouseListener myMouseListener

    Main() {
        super('CozyLand')
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
        background.setBounds(0, 0, Model.WINDOW_WIDTH, Model.WINDOW_HEIGHT)
        surface.setBounds(0, 0, Model.WINDOW_WIDTH, Model.WINDOW_HEIGHT)
        layeredPane.add(surface, 1)
        layeredPane.add(background, 2)

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