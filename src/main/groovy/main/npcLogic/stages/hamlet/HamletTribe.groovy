package main.npcLogic.stages.hamlet

import main.npcLogic.Role
import main.npcLogic.Tribe
import main.npcLogic.stages.hamlet.role.HamletChieftainRole
import main.npcLogic.stages.hamlet.role.HamletVillagerRole

class HamletTribe extends Tribe {

    @Override
    Role getNewRulerRole() {
        return new HamletChieftainRole(this)
    }

    @Override
    Role getNewVillagerRole() {
        return new HamletVillagerRole(this)
    }
}
