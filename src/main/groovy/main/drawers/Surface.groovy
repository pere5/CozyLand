package main.drawers

import main.Model
import main.Node
import main.input.MyKeyboardListener
import main.things.Drawable

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent

class Surface extends JPanel implements ActionListener {

    boolean pause = false

    int xOffset = 0
    int yOffset = 0

    @Override
    void actionPerformed(ActionEvent e) {

        MyKeyboardListener keyboard = Model.model.keyboard

        xOffset = keyboard.isKeyDown(KeyEvent.VK_LEFT) ? xOffset + 12 : xOffset
        xOffset = keyboard.isKeyDown(KeyEvent.VK_RIGHT) ? xOffset - 12 : xOffset
        yOffset = keyboard.isKeyDown(KeyEvent.VK_UP) ? yOffset + 12 : yOffset
        yOffset = keyboard.isKeyDown(KeyEvent.VK_DOWN) ? yOffset - 12 : yOffset

        if (keyboard.keyHasBeenPressed(KeyEvent.VK_SPACE)) {
            pause = !pause
        }

        if (!pause) {
            repaint()
        }
    }

    @Override
    void paintComponent(Graphics g) {
        super.paintComponent(g)
        Graphics2D g2d = (Graphics2D) g

        Node[][] nodeNetwork = Model.model.nodeNetwork

        for(int x = 0; x < nodeNetwork.length; x++) {
            for(int y = 0; y < nodeNetwork[x].length; y++) {
                def drawable = nodeNetwork[x][y] as Drawable
                g2d.setPaint(drawable.color)
                if (drawable.shape == Drawable.SHAPES.RECT ) {
                    g2d.fillRect(drawable.x + xOffset, drawable.y + yOffset, drawable.size, drawable.size)
                }
            }
        }

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