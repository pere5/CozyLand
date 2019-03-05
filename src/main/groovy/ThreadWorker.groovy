import java.awt.event.ActionEvent
import java.awt.event.ActionListener

class ThreadWorker implements ActionListener {

    def drawables
    int index

    @Override
    void actionPerformed(ActionEvent e) {
        drawables[index] = new Drawable()
    }
}
