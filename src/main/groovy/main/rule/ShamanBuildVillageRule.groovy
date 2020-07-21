package main.rule

import main.model.Villager

class ShamanBuildVillageRule extends Rule {

    /*

            def villageSpot = surveyResources.collectEntries {
                [it.key, it.value.unique { it.shape }.size()]
            }.max { it.value }.key
     */

    @Override
    int status(Villager villager) {
        def bestSpot = villager.role.tribe.surveyResources.collectEntries {
            [it.key, it.value.unique { it.shape }.size()]
        }.max { it.value }

        //trix - i - lix here

        if (bestSpot/* is good*/) {
            BAD
        } else {
            GREAT
        }
    }

    @Override
    void planWork(Villager villager, int status) {

    }

    @Override
    void toNewState(Villager villager) {

    }
}
