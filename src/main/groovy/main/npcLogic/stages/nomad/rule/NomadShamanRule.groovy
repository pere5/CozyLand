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
import main.thread.RuleWorker
import main.utility.BresenhamUtils
import main.utility.Utility

class NomadShamanRule extends Rule {

    private static final int MIN_SURVEYS = 2
    private static final int MIN_UNIQUE_RESOURCES = 2
    private static final int MIN_VILLAGERS = 5

    private static final String RULE_GOAL = 'rule_goal'

    NomadShamanRule(int rank) {
        this.rank = rank
    }

    @Override
    int status(Villager me) {
        if (me.role.tribe.location) {
            return GOOD
        } else if (me.metaObjects[RULE_GOAL]) {
            return BAD
        } else if (me.role.tribe.villagers.size() < MIN_VILLAGERS) {
            return BAD
        } else if (me.role.tribe.surveyNaturalResources.size() < MIN_SURVEYS) {
            return BAD
        } else {
            def surveyResources = me.role.tribe.surveyNaturalResources
            List<Location> goodLocations = []

            def uniqueResourcesAtSpot = surveyResources.collect {
                it.value.unique(false) { it.shape }.size()
            }.max()

            if (uniqueResourcesAtSpot < MIN_UNIQUE_RESOURCES) {
                return BAD
            } else {
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

                me.metaObjects[RULE_GOAL] = goodLocations.max { it.score }
                return BAD
            }
        }
    }

    @Override
    void planWork(Villager me, int status) {
        if (me.metaObjects[RULE_GOAL]) {
            def goodLocation = me.metaObjects[RULE_GOAL] as Location
            me.metaObjects[RULE_GOAL] = null

            me.actionQueue << new ShapeAction(Shape.SHAMAN)
            me.actionQueue << new WalkAction(goodLocation.spot)
            me.actionQueue << new ClosureAction({
                def (int tileX, int tileY) = me.getTileXY()
                def avoidThese = findWhoToAvoid(tileX, tileY, me)
                if (avoidThese) {
                    int[] tileDest = Utility.antiCentroidTile(avoidThese, me, (Main.WALK_DISTANCE_TILES_MAX / 2) as Integer)
                    def farthestPermissibleTile = BresenhamUtils.farthestPermissibleTileWithBresenham(me, tileDest, [Model.TravelType.MOUNTAIN], RuleWorker.bresenhamBuffer)
                    me.actionQueue.add(1, new WalkAction(farthestPermissibleTile))
                }
            })
            me.actionQueue << new ClosureAction({
                me.role.tribe.location = new Location(spot: me.tileXY)
            })
            me.actionQueue << new TribeAction(me.role.tribe, new HamletTribe())
        } else {
            def (int tileX, int tileY) = me.getTileXY()
            def avoidThese = findWhoToAvoid(tileX, tileY, me)
            int[] tileDest
            if (avoidThese) {
                tileDest = Utility.antiCentroidTile(avoidThese, me, Main.WALK_DISTANCE_TILES_MAX)
            } else {
                tileDest = Utility.closeRandomTile(me, me.tileXY, Main.WALK_DISTANCE_TILES_MAX, Main.WALK_DISTANCE_TILES_MIN)
            }
            def farthestPermissibleTile = BresenhamUtils.farthestPermissibleTileWithBresenham(me, tileDest, [Model.TravelType.MOUNTAIN], RuleWorker.bresenhamBuffer)
            me.actionQueue << new ShapeAction(Shape.SHAMAN)
            me.actionQueue << new WalkAction(farthestPermissibleTile)
            me.actionQueue << new ShapeAction(Shape.SHAMAN_CAMP)
            me.actionQueue << new SurveyAction(6, me.role.tribe)
        }
    }

    private List<Villager> findWhoToAvoid(int tileX, int tileY, me) {
        def tileNetwork = Model.tileNetwork
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
        return avoidThese
    }
}
