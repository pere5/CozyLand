package main.rule


import main.villager.Villager

class Walk extends Rule {

    @Override
    int status(Villager villager) {
        BAD
    }

    @Override
    void startWork(Villager villager, int status) {
        //gotta setup the actual points to travel here
        //then let PathfinderWorker calculate path between them
    }

    @Override
    void stateWhenDone(Villager villager) {
        villager.toPathfinderWorker()
    }
}
