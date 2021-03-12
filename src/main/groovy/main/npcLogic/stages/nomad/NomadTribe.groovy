package main.npcLogic.stages.nomad

import main.npcLogic.Role
import main.npcLogic.Tribe
import main.npcLogic.stages.nomad.role.NomadFollowerRole
import main.npcLogic.stages.nomad.role.NomadShamanRole

class NomadTribe extends Tribe {

    @Override
    Role getNewRulerRole() {
        return new NomadShamanRole(this)
    }

    @Override
    Role getNewVillagerRole() {
        return new NomadFollowerRole(this)
    }
}
