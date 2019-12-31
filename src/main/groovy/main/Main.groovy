package main

import main.drawers.Surface
import main.input.MyKeyboardListener
import main.input.MyMouseListener
import main.thread.InterruptionWorker
import main.thread.PathfinderWorker
import main.thread.RuleWorker
import main.thread.WorkWorker

import javax.swing.*
import java.awt.*
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

class Main extends JFrame {

    static int WINDOW_WIDTH = 1000
    static int WINDOW_HEIGHT = 750

    static int TILE_WIDTH = 8
    static int GAUSSIAN_FILTER = 3
    static int RESOURCE_PREVALENCE_TREE = 40
    static int RESOURCE_PREVALENCE_STONE = 30
    static int TREE_OFFSET_X = - (TILE_WIDTH * 2) as int
    static int TREE_OFFSET_Y = - (TILE_WIDTH * 4.2) as int
    static int STONE_OFFSET_X = - 0
    static int STONE_OFFSET_Y = - 0
    static double TREE_SCALE = 0.8
    static double STONE_SCALE = 0.30
    static double SHADE_IMAGES = 0.60

    static int MAP_WIDTH = WINDOW_WIDTH * 3
    static int MAP_HEIGHT = WINDOW_HEIGHT * 3

    static int VIEWPORT_WIDTH
    static int VIEWPORT_HEIGHT

    MyKeyboardListener myKeyboardListener
    MyMouseListener myMouseListener

    Main() {
        super('CozyLand')

        pack()
        VIEWPORT_WIDTH = WINDOW_WIDTH - (getWidth() - getContentPane().getWidth())
        VIEWPORT_HEIGHT = WINDOW_HEIGHT - (getHeight() - getContentPane().getHeight())

        myKeyboardListener = new MyKeyboardListener()
        myMouseListener = new MyMouseListener()
        Model.init(myKeyboardListener, myMouseListener)

        addKeyListener(myKeyboardListener)
        def surface = new Surface()
        surface.addMouseListener(myMouseListener)

        add(surface)

        Timer timer = new Timer(20, surface)
        timer.start()

        Thread.start {
            new WorkWorker(frameIndex: 1).run()
        }
        Thread.start {
            new RuleWorker(frameIndex: 2).run()
        }
        Thread.start {
            new PathfinderWorker(frameIndex: 3).run()
        }
        Thread.start {
            new InterruptionWorker(frameIndex: 4).run()
        }

        Model.frameSlots << [name: 'surface', fps: 0, timeSpent: 0d, string: '']
        Model.frameSlots << [name: 'work', fps: 0, timeSpent: 0d, string: '']
        Model.frameSlots << [name: 'rule', fps: 0, timeSpent: 0d, string: '']
        Model.frameSlots << [name: 'pathfinder', fps: 0, timeSpent: 0d, string: '']
        Model.frameSlots << [name: 'interruption', fps: 0, timeSpent: 0d, string: '']

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