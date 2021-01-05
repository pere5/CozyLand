package main.thread

import main.Model
import main.exception.PerIsBorkenException

abstract class Worker {

    long startTime = System.currentTimeMillis()
    int framesPerSecond = 0
    Double intendedFps = 4
    boolean isRunning = true

    int frameIndex

    def run() {

        while(isRunning) {
            if (!Model.pause) {
                long timeBeforeFrame = System.currentTimeMillis()

                update()

                //  delay for each frame - time it took for one frame
                long time = (1000 / intendedFps) - (System.currentTimeMillis() - timeBeforeFrame)
                if (time > 0) {
                    try {
                        Thread.sleep(time)
                    } catch(Exception e) {
                        throw new PerIsBorkenException()
                    }
                }

                long currentTime = System.currentTimeMillis()
                framesPerSecond++
                if (currentTime - startTime > 1000) {
                    startTime = currentTime
                    def frameSlots = Model.frameSlots
                    frameSlots[frameIndex].fps = framesPerSecond
                    frameSlots[frameIndex].timeSpent = 100 * (1 - (time / (1000 / intendedFps)))
                    framesPerSecond = 0
                }


            }
        }
    }

    abstract def update()
}
