package main.rule


import main.action.WalkAction
import main.model.Villager
import main.things.resource.Tree

class BuilderRule extends Rule {

    @Override
    int status(Villager me) {
        if (me.role.tribe.goodLocation) {
            BAD
        } else {
            GREAT
        }
    }

    @Override
    void planWork(Villager me, int status) {

        //todo: resource -> natural resource
        //todo: skapa resource som är refined
        //todo: Rock -> Stone
        //todo: Tree -> Wood

        def wood = me.role.tribe.naturalResources.count { it instanceof Tree }
        if (me.role.tribe.naturalResources)
        me.actionQueue << new WalkAction(me.role.tribe.goodLocation.spot as int[])
    }
}
