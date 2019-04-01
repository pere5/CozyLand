package main.drawers

import main.Model
import main.input.MyKeyboardListener
import main.things.Drawable

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent

class Surface extends Drawer {

    @Override
    void paintComponent(Graphics g) {
        super.paintComponent(g)
        Graphics2D g2d = (Graphics2D) g

        Model.model.drawables.each { Drawable drawable ->
            g2d.setPaint(drawable.color)
            if (drawable.shape == Drawable.SHAPES.RECT ) {
                g2d.fillRect(drawable.x + xOffset, drawable.y + yOffset, drawable.size, drawable.size)
            } else {
                g2d.fillOval(drawable.x + xOffset, drawable.y + yOffset, drawable.size, drawable.size)
            }
        }
    }
}