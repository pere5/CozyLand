package main.aStar

import javaSrc.AbstractNode

class Node extends AbstractNode {


    Node(int xPosition, int yPosition) {
        super(xPosition, yPosition)
        // do other init stuff
    }

    void sethCosts(AbstractNode endNode) {
        def dx = Math.abs(this.getxPosition() - endNode.getxPosition())
        def dy = Math.abs(this.getyPosition() - endNode.getyPosition())
        this.sethCosts( BASICMOVEMENTCOST * (dx + dy) + (DIAGONALMOVEMENTCOST - 2 * BASICMOVEMENTCOST) * Math.min(dx, dy) )
    }
}