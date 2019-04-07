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
import java.util.List

class Surface extends JPanel implements ActionListener {

    int xOffset = 0
    int yOffset = 0

    def lastFramesPerSecond = 0
    def startTime = System.currentTimeMillis()
    def framesPerSecond = 0

    @Override
    void actionPerformed(ActionEvent e) {

        MyKeyboardListener keyboard = Model.model.keyboard

        if (keyboard.keyHasBeenPressed(KeyEvent.VK_SPACE)) {
            Model.model.pause = !Model.model.pause
        }

        if (!Model.model.pause) {
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

        Image backgroundImage = Model.model.backgroundImage

        def left = - xOffset
        def right = Main.VIEWPORT_WIDTH - xOffset
        def bottom = - yOffset
        def top = Main.VIEWPORT_HEIGHT - yOffset

        g2d.drawImage(backgroundImage, xOffset, yOffset, null)

        List<Drawable> drawables = Model.model.drawables
        for (Drawable drawable : drawables) {
            if (inView(drawable, left, right, top, bottom)) {
                g2d.setPaint(drawable.color)
                if (drawable.shape == Drawable.SHAPES.RECT) {
                    g2d.fillRect(drawable.x + xOffset, drawable.y + yOffset, drawable.size, drawable.size)
                } else {
                    g2d.fillOval(drawable.x + xOffset, drawable.y + yOffset, drawable.size, drawable.size)
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
        }
        g2d.setColor(Color.CYAN);
        g2d.drawString("FPS: " + lastFramesPerSecond, 20, 20);

    }

    boolean inView(Drawable drawable, def left, def right, def top, def bottom) {
        def x = drawable.x
        def y = drawable.y
        x >= left && x <= right && y <= top && y >= bottom
    }
}