package main


import main.things.Drawable

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

class Background extends JPanel implements ActionListener  {

    @Override
    void paintComponent(Graphics g) {
        super.paintComponent(g)
        Graphics2D g2d = (Graphics2D) g

        Drawable[][] background = Model.model.background

        for(int x = 0; x < background.length; x++) {
            for(int y = 0; y < background[x].length; y++) {
                def drawable = background[x][y]
                g2d.setPaint(drawable.color)
                if (drawable.shape == Drawable.SHAPES.RECT ) {
                    g2d.fillRect(drawable.x, drawable.y, drawable.size, drawable.size)
                } else {
                    g2d.fillOval(drawable.x, drawable.y, drawable.size, drawable.size)
                }
            }
        }
        g2d.setPaint(Color.BLACK)
        g2d.fillRect(20, 20, 300, 300)
    }

    @Override
    void actionPerformed(ActionEvent e) {
        if (!Model.model.pause) {
            repaint()
        }
    }
}