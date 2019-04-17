package main.rule


import main.villager.Villager

class Walk extends Rule {

    @Override
    int status(Villager villager) {
        BAD
    }

    @Override
    void startWork(Villager villager, int status) {}

    @Override
    void setVillagerState(Villager villager) {
        villager.inPlanningPath()
    }
}
