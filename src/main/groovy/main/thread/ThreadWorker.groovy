package main.thread

import main.Model
import main.person.Person

class ThreadWorker {

    def lastFramesPerSecond = 0
    def startTime = System.currentTimeMillis()
    def framesPerSecond = 0
    def intendedFps = 8
    def isRunning = true
    int index

    def run() {

        while(isRunning) {
            long timeBeforeFrame = System.currentTimeMillis()
            //  delay for each frame - time it took for one frame
            long time = (1000 / intendedFps) - (System.currentTimeMillis() - timeBeforeFrame)
            if (time > 0) {
                try {
                    Thread.sleep(time)
                } catch(Exception e) {
                    System.out.println("Woohah!")
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
            }
        }
    }
    
    def update() {
        (Model.model.persons[index] as Person).work()
        def frameSlots = Model.model.frameSlots as List
        frameSlots[index + 1] = lastFramesPerSecond
    }
}
