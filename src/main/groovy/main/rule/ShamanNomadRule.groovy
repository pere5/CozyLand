package main.rule

import main.Main
import main.Model
import main.action.ShapeAction
import main.action.SurveyAction
import main.action.WalkAction
import main.exception.PerIsBorkenException
import main.model.Villager
import main.things.Drawable.Shape
import main.things.resource.Resource

class ShamanNomadRule extends Rule {

    private static final int NUMBER_SURVEYS = 2
    private static final int MIN_UNIQUE = 2

    @Override
    int status(Villager me) {
        def surveyResources = me.role.tribe.surveyResources
        def goodLocations = []
        if (surveyResources.size() > NUMBER_SURVEYS) {
            def maxUnique = surveyResources.collect {
                it.value.unique(false) { it.shape }.size()
            }.max()
            if (maxUnique >= MIN_UNIQUE) {

                surveyResources.each { def spot, Set<Resource> resources ->
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
                    goodLocations << [spot: spot, resources: resources, score: score]
                }
            }
        }

        if (goodLocations) {
            me.role.tribe.goodLocation = goodLocations.max { it.score }
            GOOD
        } else {
            BAD
        }
    }

    @Override
    void planWork(Villager me, int status) {
        def tileDest = Model.closeRandomTile(me, Main.SHAMAN_DISTANCE_TILES_MIN, Main.SHAMAN_DISTANCE_TILES_MAX)
        me.actionQueue << new WalkAction(tileDest)
        me.actionQueue << new ShapeAction(Shape.SHAMAN_CAMP)
        me.actionQueue << new SurveyAction(6, me.role.tribe)
        me.actionQueue << new ShapeAction(Shape.SHAMAN)
    }
}
