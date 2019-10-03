package main.rule

import main.Model
import main.action.PathfinderAction
import main.model.Tile
import main.model.Villager
import main.things.Drawable

class Affinity extends Rule {

    @Override
    int status(Villager me) {

        def tileNetwork = Model.tileNetwork as Tile[][]
        def (int villagerX, int villagerY) = me.getTileXY()

        int withinRange = 0
        Model.getPointsWithinRadii(villagerX, villagerY, Villager.COMFORT_ZONE_TILES) { int x, int y ->
            withinRange += tileNetwork[x][y].villagers.size()
        }

        if (withinRange == 0) {
            Rule.BAD
        } else if (withinRange >= 2 && withinRange <= 5) {
            Rule.GOOD
        } else if (withinRange >= 6) {
            Rule.GREAT
        } else {
            Rule.UNREACHABLE
        }
    }

    @Override
    void startWork(Villager me, int status) {
        List<Drawable> closeVillagers = []
        for (Villager villager: Model.villagers) {
            if (me.id != villager.id) {
                Double range = Model.tileRange(villager, me)
                if (range < Villager.VISIBLE_ZONE_TILES) {
                    closeVillagers << villager
                }
            }
        }

        int[] dest
        if (closeVillagers.size() == 0) {
            dest = Model.closeRandomTile(me, Villager.WALK_DISTANCE_TILES)
        } else {
            dest = Model.centroidTile(closeVillagers, me, Villager.WALK_DISTANCE_TILES)
        }

        me.actionQueue << new PathfinderAction(dest)
    }

    @Override
    void toNewState(Villager villager) {
        villager.toPathfinderWorker()
    }
}
