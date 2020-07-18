package main.thread

class InterruptionWorker extends Worker {

    @Override
    def run() {
        super.intendedFps = 3
        super.run()
    }

    def update() {
        //todo: interrupt if new role, abort all actions

    }
}
