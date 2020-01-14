package main.action

import main.model.Villager
import main.role.ShamanRole
import main.things.resource.Resource

class BuildVillageAction extends Action {

    Set<Resource> resources = []

    BuildVillageAction(List<SurveyAction> surveys) {
        
    }

    @Override
    boolean doIt(Villager me) {
        assert me instanceof Villager
        def shaman = me as Villager
        assert shaman.role instanceof ShamanRole
        
        def resolution = time > System.currentTimeMillis() ? CONTINUE : DONE
        
        return resolution
    }
}
