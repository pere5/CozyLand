package main.drawers

import main.Main
import main.Model
import main.input.MyKeyboardListener
import main.things.ArtifactLine
import main.things.Drawable

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.util.concurrent.ConcurrentLinkedQueue

class Surface extends JPanel implements ActionListener {

    static int xOffset = 0
    static int yOffset = 0

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
                if (drawable.shape == Drawable.Shape.RECT) {
                    g2d.fillRect(x, y, drawable.size, drawable.size)
                } else if (drawable.shape == Drawable.Shape.CIRCLE) {
                    g2d.fillOval(x, y, drawable.size, drawable.size)
                } else if (drawable.shape == Drawable.Shape.LINE) {
                    def artifactLine = drawable as ArtifactLine
                    def x1 = artifactLine.orig[0] + xOffset
                    def y1 = artifactLine.orig[1] + yOffset
                    def x2 = artifactLine.dest[0] + xOffset
                    def y2 = artifactLine.dest[1] + yOffset
                    g2d.drawLine(x1, y1, x2, y2)
                } else {
                    g2d.drawImage(drawable.image, x + drawable.offsetX, y + drawable.offsetY, null)
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
        if (drawable.shape == Drawable.Shape.LINE) {
            return true
        } else {
            Double x = drawable.x
            Double y = drawable.y
            x >= left && x <= right && y <= top && y >= bottom
        }
    }
}