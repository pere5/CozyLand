package main.npcLogic.stages.alone.rule

import main.Main
import main.Model
import main.model.Tile
import main.model.Villager
import main.npcLogic.Rule
import main.npcLogic.action.ShapeAction
import main.npcLogic.action.WalkAction
import main.npcLogic.stages.alone.role.AloneRole
import main.npcLogic.stages.nomad.NomadTribe
import main.npcLogic.stages.nomad.role.NomadFollowerRole
import main.npcLogic.stages.nomad.role.NomadShamanRole
import main.things.Drawable.Shape
import main.utility.Utility

import java.awt.*
import java.util.List

class AffinityRule extends Rule {

    AffinityRule(int rank) {
        this.rank = rank
    }

    @Override
    int status(Villager me) {

        def tileNetwork = Model.tileNetwork as Tile[][]
        def (int tileX, int tileY) = me.getTileXY()

        int withinRange = 0
        Utility.getTilesWithinRadii(me, tileX, tileY, Main.COMFORT_ZONE_TILES) { int x, int y ->
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
        Utility.getTilesWithinRadii(me, tileX, tileY, Main.VISIBLE_ZONE_TILES) { int x, int y ->
            tileNetwork[x][y].villagers.each { Villager villager ->
                if (villager.id != me.id) {
                    closeVillagers << villager
                }
            }
        }

        int[] tileDest
        if (closeVillagers.size() == 0) {
            tileDest = Utility.closeRandomTile(me, me.tileXY, Main.WALK_DISTANCE_TILES_MAX, Main.WALK_DISTANCE_TILES_MIN)
        } else {
            tileDest = Utility.centroidTile(closeVillagers, me, Main.WALK_DISTANCE_TILES_MAX)
        }
        me.actionQueue << new ShapeAction(Shape.WARRIOR)
        me.actionQueue << new WalkAction(tileDest, AffinityRule.&joinATribe)
    }

    static void joinATribe(Villager me) {
        if (me.role.id == AloneRole.ID) {
            def becomeFollower = { Villager villager, NomadTribe tribe ->
                villager.role = new NomadFollowerRole(tribe)
                tribe.villagers << villager
                villager.interrupt()
            }

            def tileNetwork = Model.tileNetwork
            def (int tileX, int tileY) = me.getTileXY()

            List<Villager> neighbors = []

            Utility.getTilesWithinRadii(me, tileX, tileY, Main.COMFORT_ZONE_TILES) { int x, int y ->
                tileNetwork[x][y].villagers.each { Villager neighbor ->
                    if (neighbor.id != me.id) {
                        neighbors << neighbor
                    }
                }
            }

            if (neighbors) {
                def nomadTribe = neighbors.role.tribe.find { it instanceof NomadTribe } as NomadTribe

                if (nomadTribe) {
                    becomeFollower(me, nomadTribe)
                } else {
                    Random rand = new Random()

                    NomadTribe myNomadTribe = new NomadTribe()
                    myNomadTribe.ruler = me
                    myNomadTribe.color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat())

                    me.role = new NomadShamanRole(myNomadTribe)
                    me.interrupt()
                    neighbors.findAll { it.role.id == AloneRole.ID }.each { def aloneVillager ->
                        becomeFollower(aloneVillager, myNomadTribe)
                    }
                }
            }
        }
    }
}
