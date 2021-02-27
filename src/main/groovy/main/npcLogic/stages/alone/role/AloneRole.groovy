package main.npcLogic.stages.alone.role

import main.Main
import main.Model
import main.model.Villager
import main.npcLogic.Role
import main.npcLogic.stages.generic.rule.AffinityRule
import main.npcLogic.stages.alone.AloneTribe
import main.npcLogic.Rule
import main.npcLogic.stages.nomad.NomadTribe
import main.npcLogic.stages.nomad.role.NomadFollowerRole
import main.npcLogic.stages.nomad.role.NomadShamanRole

import java.awt.Color

class AloneRole extends Role {

    static final String ID = 'alone'

    AloneRole() {
        super(ID, new AloneTribe())
    }

    List<Rule> constructRuleList() {
        int rank = Integer.MAX_VALUE
        [
                new AffinityRule(--rank, this.&joinATribe)
                //new RandomBigWalkRule(--rank)
        ]
    }

    void joinATribe(Villager me) {
        def becomeFollower = { Villager villager, NomadTribe tribe ->
            villager.role = new NomadFollowerRole(tribe)
            tribe.followers << villager
            villager.interrupt()
        }

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

                me.role = new NomadShamanRole(myNomadTribe)
                me.interrupt()
                neighbors.findAll { it.role.id == ID }.each { def aloneVillager ->
                    becomeFollower(aloneVillager, myNomadTribe)
                }
            }
        }
    }
}
