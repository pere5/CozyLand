package main.rule

import main.Main
import main.Model
import main.action.ShapeAction
import main.action.SurveyAction
import main.action.WalkAction
import main.model.Villager
import main.things.Drawable.Shape
import main.things.resource.Resource

class ShamanNomadRule extends Rule {

    private static final int NUMBER_SURVEYS = 5
    private static final int MIN_UNIQUE = 2

    private Map.Entry<int[], Set<Resource>> goodLocation

    @Override
    int status(Villager me) {
        def resources = me.role.tribe.surveyResources
        if (resources.size() > NUMBER_SURVEYS) {
            def maxUnique = resources.collect {
                it.value.unique(false) { it.shape }.size()
            }.max()
            if (maxUnique >= MIN_UNIQUE) {
                goodLocation = resources.findAll {
                    it.value.unique(false) { it.shape }.size() >= maxUnique
                }.max {
                    it.value.size()
                }
            }
        }

        if (goodLocation) {
            GOOD
        } else {
            BAD
        }
    }

    @Override
    void planWork(Villager me, int status) {
        me.actionQueue << new WalkAction(Model.closeRandomTile(me, Main.SHAMAN_DISTANCE_TILES_MIN, Main.SHAMAN_DISTANCE_TILES_MAX))
        me.actionQueue << new ShapeAction(Shape.SHAMAN_CAMP)
        me.actionQueue << new SurveyAction(6, me.role.tribe)
        me.actionQueue << new ShapeAction(Shape.SHAMAN)
    }

    @Override
    void toNewState(Villager villager) {
        villager.toPathfinderWorker()
    }
}
