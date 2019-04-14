package main.thread

import main.Model
import main.exception.PerIsBorkenException

abstract class Worker {

    def lastFramesPerSecond = 0
    def startTime = System.currentTimeMillis()
    def framesPerSecond = 0
    def intendedFps = 8
    def isRunning = true

    int frameIndex

    def run() {

        while(isRunning) {
            long timeBeforeFrame = System.currentTimeMillis()
            //  delay for each frame - time it took for one frame
            long time = (1000 / intendedFps) - (System.currentTimeMillis() - timeBeforeFrame)
            if (time > 0) {
                try {
                    Thread.sleep(time)
                } catch(Exception e) {
                    throw new PerIsBorkenException()
                }
            }

            if (!Model.model.pause) {

                update()

                long currentTime = System.currentTimeMillis()
                framesPerSecond++
                if (currentTime - startTime > 1000) {
                    startTime = currentTime
                    lastFramesPerSecond = framesPerSecond
                    framesPerSecond = 0
                }

                def frameSlots = Model.model.frameSlots as List
                frameSlots[frameIndex] = lastFramesPerSecond
            }
        }
    }

    abstract def update()
}
