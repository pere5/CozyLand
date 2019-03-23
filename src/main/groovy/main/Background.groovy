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