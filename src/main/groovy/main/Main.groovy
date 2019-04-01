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

    static int WINDOW_WIDTH = 1200
    static int WINDOW_HEIGHT = 900
    static int PANE_WIDTH
    static int PANE_HEIGHT

    MyKeyboardListener myKeyboardListener
    MyMouseListener myMouseListener

    Main() {
        super('CozyLand')

        pack()
        PANE_WIDTH = (WINDOW_WIDTH - (getWidth() - getContentPane().getWidth())) * 2
        PANE_HEIGHT = (WINDOW_HEIGHT - (getHeight() - getContentPane().getHeight())) * 2

        myKeyboardListener = new MyKeyboardListener()
        myMouseListener = new MyMouseListener()
        Model.init(myKeyboardListener, myMouseListener)

        addKeyListener(myKeyboardListener)
        def surface = new Surface()
        surface.addMouseListener(myMouseListener)
        def background = new Background()

        def layeredPane = getLayeredPane()
        background.setOpaque(false)
        surface.setOpaque(false)
        background.setBounds(0, 0, PANE_WIDTH, PANE_HEIGHT)
        surface.setBounds(0, 0, PANE_WIDTH, PANE_HEIGHT)
        layeredPane.add(surface, 1)
        layeredPane.add(background, 2)

        Timer timer = new Timer(15, surface)
        timer.start()
        Timer timer2 = new Timer(30, background)
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
                timer2.stop()
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