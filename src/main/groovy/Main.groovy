import javax.swing.*
import java.awt.*
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

class Main extends JFrame {

    static int WINDOW_WIDTH = 1000
    static int WINDOW_HEIGHT = 750

    static int idGenerator = 0
    final int DELAY = 16
    Timer timer
    MyKeyboardListener keyboardListener
    MyMouseListener myMouseListener

    Main() {
        initUI()
    }

    static int getNewId() {
        idGenerator++
    }

    def initUI() {

        final Surface surface = new Surface()

        myMouseListener = new MyMouseListener()
        surface.addMouseListener(myMouseListener)
        keyboardListener = new MyKeyboardListener()
        addKeyListener(keyboardListener)
        surface.myKeyboardListener = keyboardListener

        timer = new Timer(DELAY, surface)
        timer.start()
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
    }

    static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            @Override
            void run() {
                Main ex = new Main()
                ex.setVisible(true)
            }
        })
    }
}