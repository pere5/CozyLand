package main.drawers

import main.Model
import main.input.MyKeyboardListener

import javax.swing.JPanel
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent

class Drawer extends JPanel implements ActionListener {

    boolean pause = false

    int xOffset = 0
    int yOffset = 0

    @Override
    void actionPerformed(ActionEvent e) {

        MyKeyboardListener keyboard = Model.model.keyboard

        xOffset = keyboard.isKeyDown(KeyEvent.VK_LEFT) ? xOffset + 10 : xOffset
        xOffset = keyboard.isKeyDown(KeyEvent.VK_RIGHT) ? xOffset - 10 : xOffset
        yOffset = keyboard.isKeyDown(KeyEvent.VK_UP) ? yOffset + 10 : yOffset
        yOffset = keyboard.isKeyDown(KeyEvent.VK_DOWN) ? yOffset - 10 : yOffset

        if (keyboard.keyHasBeenPressed(KeyEvent.VK_SPACE)) {
            pause = !pause
        }

        if (!pause) {
            repaint()
        }
    }
}
