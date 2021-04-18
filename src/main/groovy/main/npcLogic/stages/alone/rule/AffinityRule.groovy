package main.npcLogic.stages.alone.rule

import main.Main
import main.Model
import main.model.Tile
import main.model.Villager
import main.npcLogic.Rule
import main.npcLogic.action.ShapeAction
import main.npcLogic.action.WalkAction
import main.npcLogic.stages.alone.AloneTribe
import main.npcLogic.stages.alone.role.AloneRole
import main.npcLogic.stages.nomad.NomadTribe
import main.utility.GameUtils
import main.utility.Utility

class AffinityRule extends Rule {

    AffinityRule(int rank) {
        this.rank = rank
    }

    @Override
    int status(Villager me) {

        def tileNetwork = Model.tileNetwork as Tile[][]
        def (int tileX, int tileY) = me.getTileXY()

        int withinRange = 0
        Utility.getTilesWithinRadii(tileX, tileY, Main.COMFORT_ZONE_TILES) { int x, int y ->
            withinRange += tileNetwork[x][y].villagers.size()
        }

        if (withinRange == 0) {
            BAD
        } else if (withinRange >= 2 && withinRange <= 5) {
            GOOD
        } else if (withinRange >= 6) {
            GREAT
        } else {
            UNREACHABLE
        }
    }

    @Override
    void planWork(Villager me, int status) {
        def tileNetwork = Model.tileNetwork as Tile[][]
        def (int tileX, int tileY) = me.getTileXY()

        List<Villager> closeVillagers = []
        Utility.getTilesWithinRadii(tileX, tileY, Main.VISIBLE_ZONE_TILES) { int x, int y ->
            tileNetwork[x][y].villagers.each { Villager villager ->
                if (villager.id != me.id) {
                    closeVillagers << villager
                }
            }
        }

        int[] tileDest
        if (closeVillagers) {
            tileDest = Utility.centroidTile(closeVillagers, me, Main.WALK_DISTANCE_TILES_MAX)
        } else {
            tileDest = Utility.closeRandomTile(me, me.tileXY, Main.WALK_DISTANCE_TILES_MAX, Main.WALK_DISTANCE_TILES_MIN)
        }
        me.actionQueue << new ShapeAction(Model.Shape.WARRIOR_STAGE_1)
        me.actionQueue << new WalkAction(tileDest, AffinityRule.&joinATribe)
    }

    static void joinATribe(Villager me) {
        if (me.role.id == AloneRole.ID) {
            def tileNetwork = Model.tileNetwork
            def (int tileX, int tileY) = me.getTileXY()
            List<Villager> neighbors = []

            Utility.getTilesWithinRadii(tileX, tileY, Main.COMFORT_ZONE_TILES) { int x, int y ->
                tileNetwork[x][y].villagers.each { Villager neighbor ->
                    if (neighbor.id != me.id) {
                        neighbors << neighbor
                    }
                }
            }

            if (neighbors) {
                def otherTribe = neighbors.role.tribe.find { !(it instanceof AloneTribe) }
                if (otherTribe) {
                    GameUtils.joinTribe(me, otherTribe)
                } else {
                    neighbors.each {
                        GameUtils.joinTribe(it, me.role.tribe)
                    }
                    GameUtils.transformTribe(me.role.tribe, new NomadTribe())
                }
            }
        }
    }
}
