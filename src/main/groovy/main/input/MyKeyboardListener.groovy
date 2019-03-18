package main.input

import main.Model

import java.awt.event.KeyEvent
import java.awt.event.KeyListener

/**
 * Makes handling keyboard a lot simpler
 */
class MyKeyboardListener implements KeyListener {

    private boolean [] keys = new boolean[256]
    private boolean [] pressedKeys = new boolean[256]

    /**
     * Checks whether a specific key has been pressed since last check
     * @param keyCode The key to check
     * @return Whether the key has been pressed or not since last check
     */
    boolean keyHasBeenPressed(int keyCode) {
        boolean keyHasBeenPressed = keyCode > 0 && keyCode < 256 && pressedKeys[keyCode]
        pressedKeys[keyCode] = false
        return keyHasBeenPressed
    }

    /**
     * Checks whether a specific key is down
     * @param keyCode The key to check
     * @return Whether the key is pressed or not
     */
    boolean isKeyDown(int keyCode) {
        return keyCode > 0 && keyCode < 256 && keys[keyCode]
    }

    /**
     * Called when a key is pressed while the component is focused
     * @param e KeyEvent sent by the component
     */
    void keyPressed(KeyEvent e) {
        if (e.getKeyCode() > 0 && e.getKeyCode() < 256) {
            keys[e.getKeyCode()] = true
            pressedKeys[e.getKeyCode()] = true
        }

        if (keyHasBeenPressed(KeyEvent.VK_SPACE)) {
            Model.model.pause = !Model.model.pause
        }
    }

    /**
     * Called when a key is released while the component is focused
     * @param e KeyEvent sent by the component
     */
    void keyReleased(KeyEvent e) {
        if (e.getKeyCode() > 0 && e.getKeyCode() < 256) {
            keys[e.getKeyCode()] = false
        }
    }

    /**
     * Not used
     */
    void keyTyped(KeyEvent e) {

    }
}