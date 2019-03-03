import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

class Surface extends JPanel implements ActionListener {

    final int DELAY = 150
    Timer timer

    Surface() {
        initTimer()
    }

    def initTimer() {
        timer = new Timer(DELAY, this)
        timer.start()
    }

    Timer getTimer() {
        return timer
    }

    def doDrawing(Graphics g) {

        Graphics2D g2d = (Graphics2D) g

        g2d.setPaint(Color.blue)

        int w = getWidth()
        int h = getHeight()

        Random r = new Random()

        (0..200).each {

            int x = Math.abs(r.nextInt()) % w
            int y = Math.abs(r.nextInt()) % h
            g2d.drawLine(x, y, x, y)
        }
    }

    @Override
    void paintComponent(Graphics g) {

        super.paintComponent(g)
        doDrawing(g)
    }

    @Override
    void actionPerformed(ActionEvent e) {
        repaint()
    }
}