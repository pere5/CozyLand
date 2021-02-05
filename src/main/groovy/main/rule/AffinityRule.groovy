package main.rule


import main.Main
import main.Model
import main.action.ShapeAction
import main.action.WalkAction
import main.model.Tile
import main.model.Villager
import main.role.alone.AloneRole
import main.role.tribe.FollowerRole
import main.role.tribe.NomadTribe
import main.role.tribe.ShamanRole
import main.things.Drawable.Shape

import java.awt.Color

class AffinityRule extends Rule {

    @Override
    int status(Villager me) {

        def tileNetwork = Model.tileNetwork as Tile[][]
        def (int tileX, int tileY) = me.getTileXY()

        int withinRange = 0
        Model.getTilesWithinRadii(me, tileX, tileY, Main.COMFORT_ZONE_TILES) { int x, int y ->
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
        Model.getTilesWithinRadii(me, tileX, tileY, Main.VISIBLE_ZONE_TILES) { int x, int y ->
            tileNetwork[x][y].villagers.each { Villager villager ->
                if (villager.id != me.id) {
                    closeVillagers << villager
                }
            }
        }

        int[] tileDest
        if (closeVillagers.size() == 0) {
            tileDest = Model.closeRandomTile(me, me.tileXY, Main.WALK_DISTANCE_TILES_MAX, Main.WALK_DISTANCE_TILES_MIN)
        } else {
            tileDest = Model.centroidTile(closeVillagers, me, Main.WALK_DISTANCE_TILES_MAX)
        }
        me.actionQueue << new ShapeAction(Shape.WARRIOR)
        me.actionQueue << new WalkAction(tileDest, this.&joinATribe)
    }

    void joinATribe(Villager me) {
        def tileNetwork = Model.tileNetwork
        def (int tileX, int tileY) = me.getTileXY()

        List<Villager> neighbors = []

        Model.getTilesWithinRadii(me, tileX, tileY, Main.COMFORT_ZONE_TILES) { int x, int y ->
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
                myNomadTribe.shaman = me
                myNomadTribe.color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat())

                me.role = new ShamanRole(myNomadTribe)
                me.interrupt()
                neighbors.findAll { it.role.id == AloneRole.ID }.each { def aloneVillager ->
                    becomeFollower(aloneVillager, myNomadTribe)
                }
            }
        }
    }

    void becomeFollower (Villager villager, NomadTribe tribe) {
        villager.role = new FollowerRole(tribe)
        tribe.followers << villager
        villager.interrupt()
    }
}
