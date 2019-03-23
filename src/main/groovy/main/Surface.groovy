package main


import main.things.Drawable

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

class Surface extends JPanel implements ActionListener {

    boolean first = true

    @Override
    void paintComponent(Graphics g) {
        super.paintComponent(g)
        Graphics2D g2d = (Graphics2D) g

        Model.model.drawables.each { Drawable drawable ->
            g2d.setPaint(drawable.color)
            if (drawable.shape == Drawable.SHAPES.RECT ) {
                g2d.fillRect(drawable.x, drawable.y, drawable.size, drawable.size)
            } else {
                g2d.fillOval(drawable.x, drawable.y, drawable.size, drawable.size)
            }
        }
    }

    @Override
    void actionPerformed(ActionEvent e) {
        if (!Model.model.pause) {
            repaint()
        }
    }
}