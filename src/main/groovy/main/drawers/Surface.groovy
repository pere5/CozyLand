package main.drawers

import main.Main
import main.Model
import main.input.MyKeyboardListener
import main.things.Drawable

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.util.concurrent.ConcurrentLinkedQueue

class Surface extends JPanel implements ActionListener {

    int xOffset = 0
    int yOffset = 0

    int lastFramesPerSecond = 0
    long startTime = System.currentTimeMillis()
    int framesPerSecond = 0

    @Override
    void actionPerformed(ActionEvent e) {

        MyKeyboardListener keyboard = Model.keyboard

        if (keyboard.keyHasBeenPressed(KeyEvent.VK_SPACE)) {
            Model.pause = !Model.pause
        }

        if (!Model.pause) {
            xOffset = keyboard.isKeyDown(KeyEvent.VK_LEFT) ? xOffset + 12 : xOffset
            xOffset = keyboard.isKeyDown(KeyEvent.VK_RIGHT) ? xOffset - 12 : xOffset
            yOffset = keyboard.isKeyDown(KeyEvent.VK_UP) ? yOffset + 12 : yOffset
            yOffset = keyboard.isKeyDown(KeyEvent.VK_DOWN) ? yOffset - 12 : yOffset

            repaint()
        }
    }

    @Override
    void paintComponent(Graphics g) {
        super.paintComponent(g)
        Graphics2D g2d = (Graphics2D) g

        int left = - xOffset
        int right = Main.VIEWPORT_WIDTH - xOffset
        int bottom = - yOffset
        int top = Main.VIEWPORT_HEIGHT - yOffset

        g2d.drawImage(Model.backgroundImage, xOffset, yOffset, null)

        ConcurrentLinkedQueue<Drawable> drawables = Model.drawables
        for (Drawable drawable : drawables) {
            if (inView(drawable, left, right, top, bottom)) {
                g2d.setPaint(drawable.color)
                def x = (drawable.x + xOffset) as int
                def y = (drawable.y + yOffset) as int
                def size = drawable.size
                if (drawable.shape == Drawable.SHAPES.RECT) {
                    g2d.fillRect(x, y, size, size)
                } else if (drawable.shape == Drawable.SHAPES.CIRCLE) {
                    g2d.fillOval(x, y, size, size)
                } else if (drawable.shape == Drawable.SHAPES.TREE) {
                    g2d.drawImage(drawable.image, x + Main.TREE_OFFSET_X, y + Main.TREE_OFFSET_Y, null)
                } else if (drawable.shape == Drawable.SHAPES.STONE) {
                    g2d.drawImage(drawable.image, x + Main.STONE_OFFSET_X, y + Main.STONE_OFFSET_Y, null)
                } else if (drawable.shape in [Drawable.SHAPES.BASE, Drawable.SHAPES.SHAMAN, Drawable.SHAPES.FOLLOWER]) {
                    g2d.drawImage(drawable.image, x + Main.PERSON_OFFSET_X, y + Main.PERSON_OFFSET_Y, null)
                }
            }
        }

        drawFPS(g2d)
    }

    void drawFPS(Graphics2D g2d) {
        long currentTime = System.currentTimeMillis()
        framesPerSecond++
        if (currentTime - startTime > 1000) {
            startTime = currentTime
            lastFramesPerSecond = framesPerSecond
            framesPerSecond = 0
            Model.frameSlots[0].fps = lastFramesPerSecond

            for (int i = 0; i < Model.frameSlots.size(); i++) {
                def fps = "${Model.frameSlots[i].name}: ${Model.frameSlots[i].fps}"
                def timeSpent = ", ${String.format("%.2f", Model.frameSlots[i].timeSpent)}%"

                Model.frameSlots[i].string = i == 0 ? fps : (fps + timeSpent)
            }
        }
        g2d.setColor(Color.CYAN)

        for (int i = 0; i < Model.frameSlots.size(); i++) {
            g2d.drawString(Model.frameSlots[i].string as String, 20, 20 * (i + 1))
        }
    }

    boolean inView(Drawable drawable, int left, int right, int top, int bottom) {
        Double x = drawable.x
        Double y = drawable.y
        x >= left && x <= right && y <= top && y >= bottom
    }
}