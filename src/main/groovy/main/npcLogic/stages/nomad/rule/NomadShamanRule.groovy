package main.npcLogic.stages.nomad.rule

import main.Main
import main.calculator.Utility
import main.model.Location
import main.model.Villager
import main.npcLogic.Rule
import main.npcLogic.action.ClosureAction
import main.npcLogic.action.ShapeAction
import main.npcLogic.action.SurveyAction
import main.npcLogic.action.WalkAction
import main.things.Drawable.Shape
import main.things.naturalResource.NaturalResource

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
            me.actionQueue << new ClosureAction({
                me.role.tribe.goodLocation = location
                me.metaObjects[NomadShamanRule.toString()] = null
            })
        } else {
            def tileDest = Utility.closeRandomTile(me, me.tileXY, Main.SHAMAN_DISTANCE_TILES_MAX, Main.SHAMAN_DISTANCE_TILES_MIN)
            me.actionQueue << new ShapeAction(Shape.SHAMAN)
            me.actionQueue << new WalkAction(tileDest)
            me.actionQueue << new ShapeAction(Shape.SHAMAN_CAMP)
            me.actionQueue << new SurveyAction(6, me.role.tribe)
        }
    }
}
