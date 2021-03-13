package main.npcLogic.action

import main.model.Villager
import main.npcLogic.Action
import main.things.building.home.Home

class HomeAction extends Action {

    Class<? extends Home> homeClazz

    HomeAction(Class<? extends Home> homeClazz) {
        this.homeClazz = homeClazz
    }

    @Override
    boolean interrupt() {
        return false
    }

    @Override
    void switchWorker(Villager me) {
        throw new UnsupportedOperationException()
    }

    @Override
    boolean doIt(Villager me) {
        homeClazz.getDeclaredConstructor(Villager.class).newInstance(me)
        DONE
    }
}
