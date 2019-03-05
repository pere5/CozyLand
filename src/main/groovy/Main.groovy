import javax.swing.*
import java.awt.*
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

class Main extends JFrame {

    static int WINDOW_WIDTH = 1000
    static int WINDOW_HEIGHT = 750

    static int idGenerator = 0
    MyKeyboardListener keyboardListener
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
        keyboardListener = new MyKeyboardListener()
        addKeyListener(keyboardListener)
        surface.myKeyboardListener = keyboardListener

        Timer timer = new Timer(150, surface)
        timer.start()

        //https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/uiswing/examples/concurrency/FlipperProject/src/concurrency/Flipper.java

        Timer timer2 = new Timer(500, new ThreadWorker(index: 0, drawables: drawables))
        timer2.start()
        Timer timer3 = new Timer(700, new ThreadWorker(index: 1, drawables: drawables))
        timer3.start()
        Timer timer4 = new Timer(900, new ThreadWorker(index: 2, drawables: drawables))
        timer4.start()
        Timer timer5 = new Timer(1100, new ThreadWorker(index: 3, drawables: drawables))
        timer5.start()
        Timer timer6 = new Timer(1300, new ThreadWorker(index: 4, drawables: drawables))
        timer6.start()
        add(surface)

        addWindowListener(new WindowAdapter() {
            @Override
            void windowClosing(WindowEvent e) {
                timer.stop()
                timer2.stop()
                timer3.stop()
                timer4.stop()
                timer5.stop()
                timer6.stop()
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