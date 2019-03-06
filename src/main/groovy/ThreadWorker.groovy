import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent

class ThreadWorker {

    MyKeyboardListener myKeyboardListener

    def pause = false
    def lastFramesPerSecond = 0
    def startTime = System.currentTimeMillis()
    def framesPerSecond = 0
    def intendedFps = 8
    def isRunning = true
    def drawables
    int index

    void work() {

        while(isRunning) {
            boolean spaceHasBeenPressed = myKeyboardListener.keyHasBeenPressed(KeyEvent.VK_SPACE)
            if (spaceHasBeenPressed) {
                pause = !pause
            }

            long timeBeforeFrame = System.currentTimeMillis()

            update()

            //  delay for each frame  -   time it took for one frame
            long time = (1000 / intendedFps) - (System.currentTimeMillis() - timeBeforeFrame)
            if (time > 0) {
                try {
                    Thread.sleep(time)
                } catch(Exception e) {
                    System.out.println("Woohah!")
                }
            }
            if (!pause) {
                long currentTime = System.currentTimeMillis()
                framesPerSecond++
                if (currentTime - startTime > 1000) {
                    startTime = currentTime
                    lastFramesPerSecond = framesPerSecond
                    framesPerSecond = 0
                }
            }
        }
    }
    
    def update() {
        drawables[index] = new Drawable()
    }
}
