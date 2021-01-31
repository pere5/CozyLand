package main.action

import main.Model
import main.exception.PerIsBorkenException
import main.model.StraightPath
import main.model.Tile
import main.model.Villager
import main.things.Drawable

class WalkAction extends Action {

    int[] tileDest
    Closure closure

    //this is populated by pathfinderWorker
    Queue<StraightPath> pathQueue = new LinkedList<>()

    WalkAction(int[] tile) {
        super(false)
        tileDest = tile
    }

    WalkAction(int[] tile, Closure closure) {
        super(false)
        tileDest = tile
        this.closure = closure
    }

    @Override
    void switchWorker(Villager villager) {
        villager.toPathfinderWorker()
    }

    @Override
    boolean doIt(Villager villager) {
        Boolean result
        def straightPath = pathQueue.peek()
        if (straightPath) {
            def step = straightPath.path.poll()
            if (step) {
                def (Double x, Double y) = step
                villager.x = x
                villager.y = y
                result = CONTINUE
            } else {
                pathQueue.poll()
                result = doIt(villager)
            }
        } else {
            result = DONE
        }

        if (result == DONE) {
            placeVillagersInTileNetwork(villager)
        } else {
            perTenSeconds (10) {
                placeVillagersInTileNetwork(villager)
            }
        }

        return result
    }


    private void placeVillagersInTileNetwork(Villager villager) {

        def (int tileX, int tileY) = villager.getTileXY()
        def correctTile = Model.tileNetwork[tileX][tileY] as Tile

        if (!villager.tile) {
            villager.tile = correctTile
            villager.tile.villagers << villager
        } else if (correctTile.id != villager.tile.id) {
            villager.tile.villagers.remove(villager)
            villager.tile = correctTile
            villager.tile.villagers << villager
        }

        def test = false
        if (test) {
            def tileNetwork = Model.tileNetwork
            def matches = []
            for (int x = 0; x < tileNetwork.length; x++) {
                for (int y = 0; y < tileNetwork[x].length; y++) {
                    Tile tile = tileNetwork[x][y]
                    if (villager.id in tile.villagers.id) {
                        matches << [tile: tile, villager: villager]
                    }
                }
            }

            def one = correctTile.id == villager.tile.id
            def two = villager.id == villager.tile.villagers.find { it.id == villager.id }?.id
            def three = matches.size() == 1
            def four = villager.id in (matches.find().tile as Tile).villagers.id

            if (!(one && two && three && four)) {
                throw new PerIsBorkenException()
            }
        }
    }
}
