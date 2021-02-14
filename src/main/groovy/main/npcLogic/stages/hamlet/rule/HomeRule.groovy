package main.npcLogic.stages.hamlet.rule

import main.Main
import main.Model
import main.model.Villager
import main.npcLogic.stages.nomad.NomadTribe
import main.npcLogic.Rule
import main.things.building.Hut

class HomeRule extends Rule {

    @Override
    int status(Villager me) {
        if (!me.home && me.role.tribe.goodLocation) {
            BAD
        } else {
            GREAT
        }
    }

    @Override
    void planWork(Villager me, int status) {

        def tribe = me.role.tribe as NomadTribe
        int[] tileXY = Model.closeRandomTile(me, tribe.shaman.tileXY, Main.COMFORT_ZONE_TILES + 1, 1)
        me.home = new Hut(me, tileXY)
    }
}
