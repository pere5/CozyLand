package main.rule

import main.Main
import main.Model
import main.model.Villager
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

        //loop so that it isn't close to their huts...
        def mySpot = me.tileXY
        def tileXY
        for (int i = 0; i < 20; i++) {
            def candidate = Model.closeRandomTile(me, mySpot, Main.COMFORT_ZONE_TILES + 1, 1)
            if (!Model.tilesEqual(candidate, mySpot)) {
                tileXY = candidate
                break
            }
        }

        if (tileXY) {
            me.home = new Hut(me, tileXY)
        } else {
            //hej ho
        }

    }
}
