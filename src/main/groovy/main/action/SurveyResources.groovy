package main.action

import main.things.Drawable

class SurveyResources extends Action {
    @Override
    boolean doIt(Drawable me) {
        /*
        def tileNetwork = Model.tileNetwork as Tile[][]
        List<Resource> resources = []
        shaman.role.villagers.each { def follower ->
            def (int followerX, int followerY) = follower.getTileXY()
            Model.getPointsWithinRadii(followerX, followerY, Villager.VISIBLE_ZONE_TILES) { int x, int y ->
                Tile tile = tileNetwork[x][y]
                resources.addAll(tile.resources)
            }
        }
        */

        return false
    }
}
