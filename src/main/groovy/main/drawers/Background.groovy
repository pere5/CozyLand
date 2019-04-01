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

class Background extends Drawer  {

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
    }
}