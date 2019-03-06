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

        def drawables = [new Drawable(),new Drawable(),new Drawable(),new Drawable(),new Drawable()]

        final Surface surface = new Surface(drawables: drawables)

        myMouseListener = new MyMouseListener()
        surface.addMouseListener(myMouseListener)
        myKeyboardListener = new MyKeyboardListener()
        addKeyListener(myKeyboardListener)
        surface.myKeyboardListener = myKeyboardListener

        Timer timer = new Timer(50, surface)
        timer.start()

        //https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/uiswing/examples/concurrency/FlipperProject/src/concurrency/Flipper.java

        Thread.start {
            new ThreadWorker(index: 0, drawables: drawables, myKeyboardListener: myKeyboardListener).work()
        }
        Thread.start {
            new ThreadWorker(index: 1, drawables: drawables, myKeyboardListener: myKeyboardListener).work()
        }
        Thread.start {
            new ThreadWorker(index: 2, drawables: drawables, myKeyboardListener: myKeyboardListener).work()
        }
        Thread.start {
            new ThreadWorker(index: 3, drawables: drawables, myKeyboardListener: myKeyboardListener).work()
        }
        Thread.start {
            new ThreadWorker(index: 4, drawables: drawables, myKeyboardListener: myKeyboardListener).work()
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