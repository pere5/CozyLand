package main.npcLogic.action


import main.model.Villager
import main.npcLogic.Action
import main.things.building.Building

class HomeAction extends Action {

    Class<? extends Building> buildingClazz

    HomeAction(Class<? extends Building> buildingClazz) {
        this.buildingClazz = buildingClazz
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
        me.home = buildingClazz.getDeclaredConstructor(Villager.class).newInstance(me)
        DONE
    }
}
