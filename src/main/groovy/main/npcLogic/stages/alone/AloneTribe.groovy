package main.npcLogic.stages.alone


import main.npcLogic.Role
import main.npcLogic.Tribe
import main.npcLogic.stages.alone.role.AloneRole

class AloneTribe extends Tribe {

    @Override
    Role getNewRulerRole() {
        return new AloneRole(this)
    }

    @Override
    Role getNewVillagerRole() {
        return new AloneRole(this)
    }
}
