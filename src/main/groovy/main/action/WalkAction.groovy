package main.action

import main.Model
import main.exception.PerIsBorkenException
import main.model.StraightPath
import main.model.Tile
import main.model.Villager

class WalkAction extends Action {

    int[] tileDest

    //this is populated by pathfinderWorker
    Queue<StraightPath> pathQueue = new LinkedList<>()

    WalkAction(int[] tile) {
        super(false)
        tileDest = tile
    }

    WalkAction(int[] tile, Closure closure) {
        super(false, closure)
        tileDest = tile
    }

    @Override
    boolean interrupt() {
        def first = pathQueue.find()
        if (first) {
            pathQueue.clear()
            pathQueue << first
            return true
        } else {
            return false
        }
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
            placeInTileNetwork(villager)
        } else {
            perInterval (1000, 1) {
                placeInTileNetwork(villager)
            }
        }

        if (closure) {
            perInterval(1000, 2) {
                closure(villager)
            }
        }

        return result
    }

    private void placeInTileNetwork(Villager villager) {

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
