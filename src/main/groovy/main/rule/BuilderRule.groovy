package main.rule


import main.action.WalkAction
import main.model.Villager
import main.things.naturalResource.Tree

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

        //todo: naturalResource -> natural naturalResource
        //todo: skapa naturalResource som är refined
        //todo: Rock -> Stone
        //todo: Tree -> Wood

        def wood = me.role.tribe.resources.count { it instanceof Tree }
        if (me.role.tribe.resources)
        me.actionQueue << new WalkAction(me.role.tribe.goodLocation.spot as int[])
    }
}
