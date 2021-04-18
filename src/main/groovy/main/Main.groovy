package main

import main.drawers.Surface
import main.input.MyKeyboardListener
import main.input.MyMouseListener
import main.thread.ActionWorker
import main.thread.InterruptionWorker
import main.thread.PathfinderWorker
import main.thread.RuleWorker
import main.utility.OSUtils

import javax.swing.*
import java.awt.*
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

class Main extends JFrame {

    static int WINDOW_WIDTH = 1000
    static int WINDOW_HEIGHT = 750

    static final int TILE_WIDTH = 9
    static final int MAP_WIDTH = (1000 * 7) as Integer
    static final int MAP_HEIGHT = (750 * 7) as Integer
    static int VIEWPORT_WIDTH
    static int VIEWPORT_HEIGHT

    static final int COMFORT_ZONE_TILES = 2
    static final int VISIBLE_ZONE_TILES = 10
    static final int WALK_DISTANCE_TILES_MAX = 8
    static final int WALK_DISTANCE_TILES_MIN = 6
    static final int SHAMAN_DISTANCE_TILES_MAX = 15
    static final int SHAMAN_DISTANCE_TILES_MIN = 10

    static final Double STEP = 0.85

    static final int GAUSSIAN_FILTER = 4

    static final Double SHADE_IMAGES = 0.75
    static final Double SHADE_TRIBE = 2.0

    MyKeyboardListener myKeyboardListener
    MyMouseListener myMouseListener

    Main() {
        super('CozyLand')

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
            new ActionWorker(frameIndex: 1).run()
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

        addComponentListener(new MyComponentListener())
        setLocationRelativeTo(null)
        setDefaultCloseOperation(EXIT_ON_CLOSE)
        setExtendedState(MAXIMIZED_BOTH)
        setVisible(true)
        if (!OSUtils.isWindows()) {
            pack()
            toFront()
            requestFocus()
        }
    }

    class MyComponentListener implements ComponentListener {
        void componentHidden(ComponentEvent e) { }

        void componentMoved(ComponentEvent e) { }

        void componentResized(ComponentEvent e) {
            println(e.getComponent().getClass().getName() + " --- Resized ");
            Dimension size = e.getComponent().getBounds().getSize()
            WINDOW_HEIGHT = (size.height as Integer)
            WINDOW_WIDTH = (size.width as Integer)
            VIEWPORT_WIDTH = WINDOW_WIDTH - (getWidth() - getContentPane().getWidth())
            VIEWPORT_HEIGHT = WINDOW_HEIGHT - (getHeight() - getContentPane().getHeight())
        }

        void componentShown(ComponentEvent e) { }
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