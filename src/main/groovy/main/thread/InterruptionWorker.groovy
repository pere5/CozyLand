package main.thread

import main.Model
import main.npcLogic.stages.nomad.NomadTribe
import main.npcLogic.stages.nomad.role.NomadShamanRole
import main.things.Drawable

class InterruptionWorker extends Worker {

    @Override
    def run() {
        super.intendedFps = 0.25
        super.run()
    }

    def update() {
        def test = false
        if (test) {
            def weird = Model.villagers.findAll {
                def tribe = it.role?.tribe
                if (tribe instanceof NomadTribe) {
                    def nomadTribe = tribe as NomadTribe
                    return !nomadTribe.shaman
                }
                return false
            }

            if (weird) {
                int lol = 0
            }

            def weird2 = Model.villagers.findAll {
                if (it.role.id == NomadShamanRole.ID) {
                    return it.shape != Drawable.Shape.SHAMAN &&
                            it.shape != Drawable.Shape.SHAMAN_CAMP &&
                            it.shape != Drawable.Shape.SHAMAN_BUILD
                }
                return false
            }

            if (weird2) {
                int lol = 0
            }

            def weird3 = Model.villagers.findAll {
                if (it.role.id == NomadShamanRole.ID) {
                    def tribe = it.role.tribe as NomadTribe
                    return tribe.shaman.id != it.id
                }
                return false
            }

            if (weird3) {
                int lol = 0
            }
        }
    }
}
