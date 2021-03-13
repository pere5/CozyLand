package main.npcLogic.stages.nomad.rule

import main.Main
import main.Model
import main.model.Location
import main.model.Villager
import main.npcLogic.Rule
import main.npcLogic.action.*
import main.npcLogic.stages.alone.role.AloneRole
import main.npcLogic.stages.hamlet.HamletTribe
import main.things.Drawable.Shape
import main.things.naturalResource.NaturalResource
import main.utility.Utility

class NomadShamanRule extends Rule {

    private static final int NUMBER_SURVEYS = 2
    private static final int MIN_UNIQUE = 2

    NomadShamanRule(int rank) {
        this.rank = rank
    }

    @Override
    int status(Villager me) {
        if (me.role.tribe.goodLocation) {
            return GOOD
        } else if (me.metaObjects[NomadShamanRule.toString()]) {
            return BAD
        } else {
            def surveyResources = me.role.tribe.surveyNaturalResources
            List<Location> goodLocations = []
            if (surveyResources.size() > NUMBER_SURVEYS) {
                def maxUnique = surveyResources.collect {
                    it.value.unique(false) { it.shape }.size()
                }.max()
                if (maxUnique >= MIN_UNIQUE) {

                    surveyResources.each { def spot, Set<NaturalResource> resources ->
                        def multiplier = 1
                        def counted = resources.countBy {
                            it.shape
                        }
                        def list = counted.collect {
                            [shape: it.key, amount: it.value]
                        }
                        def sorted = list.sort {
                            -it.amount
                        }
                        def multiplied = sorted.collect {
                            it.amount *= multiplier
                            multiplier *= 3
                            return it
                        }
                        def score = multiplied.sum {
                            it.amount
                        } as Integer
                        goodLocations << new Location(spot: spot as int[], naturalResources: resources, score: score)
                    }
                }
            }

            if (goodLocations) {
                me.metaObjects[NomadShamanRule.toString()] = goodLocations.max { it.score }
            }

            return BAD
        }
    }

    @Override
    void planWork(Villager me, int status) {
        if (me.metaObjects[NomadShamanRule.toString()]) {
            def location = me.metaObjects[NomadShamanRule.toString()] as Location
            me.actionQueue << new ShapeAction(Shape.SHAMAN)
            me.actionQueue << new WalkAction(location.spot)
            //me.actionQueue << new DistanceAction()
            me.actionQueue << new ClosureAction({
                me.role.tribe.goodLocation = location
                me.metaObjects[NomadShamanRule.toString()] = null
            })
            me.actionQueue << new TribeAction(me.role.tribe, new HamletTribe())
        } else {
            def tileNetwork = Model.tileNetwork
            def (int tileX, int tileY) = me.getTileXY()
            def avoidThese = []
            Utility.getTilesWithinRadii(tileX, tileY, Main.VISIBLE_ZONE_TILES) { int x, int y ->
                tileNetwork[x][y].villagers.each { Villager villager ->
                    def notMyTribe = villager.role.tribe.id != me.role.tribe.id
                    def notAlone = villager.role.id != AloneRole.ID
                    if (notMyTribe && notAlone) {
                        avoidThese << villager
                    }
                }
            }
            int[] tileDest
            if (avoidThese) {
                tileDest = Utility.antiCentroidTile(avoidThese, me, Main.WALK_DISTANCE_TILES_MAX)
            } else {
                tileDest = Utility.closeRandomTile(me, me.tileXY, Main.WALK_DISTANCE_TILES_MAX, Main.WALK_DISTANCE_TILES_MIN)
            }
            me.actionQueue << new ShapeAction(Shape.SHAMAN)
            me.actionQueue << new WalkAction(tileDest)
            me.actionQueue << new ShapeAction(Shape.SHAMAN_CAMP)
            me.actionQueue << new SurveyAction(6, me.role.tribe)
        }
    }
}
