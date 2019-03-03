import javax.swing.*
import java.awt.*
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

class Main extends JFrame {

    Main() {
        initUI()
    }

    def initUI() {

        final Surface surface = new Surface()
        add(surface)

        addWindowListener(new WindowAdapter() {
            @Override
            void windowClosing(WindowEvent e) {
                Timer timer = surface.getTimer()
                timer.stop()
            }
        })

        setTitle("Points")
        setSize(350, 250)
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