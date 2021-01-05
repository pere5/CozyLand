package main.action

import main.Model
import main.model.StraightPath
import main.model.Tile
import main.model.Villager

class WalkAction extends Action {

    int[] tileDest

    //this is populated by pathfinderWorker
    Queue<StraightPath> pathQueue = new LinkedList<>()

    WalkAction(int[] tile) {
        this.initialized = false
        tileDest = tile
    }

    @Override
    void switchWorker(Villager villager) {
        villager.toPathfinderWorker()
    }

    @Override
    boolean doIt(Villager villager) {
        def straightPath = pathQueue.peek()
        if (straightPath) {
            def step = straightPath.path.poll()
            if (step) {
                def (Double x, Double y) = step
                villager.x = x
                villager.y = y
                placeVillagersInTileNetwork(villager)
                return CONTINUE
            } else {
                pathQueue.poll()
                return doIt(villager)
            }
        } else {
            return DONE
        }
    }


    private void placeVillagersInTileNetwork(Villager villager) {


        //geh here

        def (int tileX, int tileY) = villager.getTileXY()
        def tile = Model.tileNetwork[tileX][tileY] as Tile
        if (tileX != x || tileY != y) {
            tile.villagers.remove(villager)
            tileNetwork[tileX][tileY].villagers << villager
        }
    }
}
