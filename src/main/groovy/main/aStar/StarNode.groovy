package main.aStar

import javaSrc.AbstractNode

class StarNode extends AbstractNode {

    StarNode(int xPosition, int yPosition) {
        super(xPosition, yPosition)
    }

    void sethCosts(AbstractNode endNode) {
        this.sethCosts(
                (Math.abs(this.getxPosition() - endNode.getxPosition())
                        + Math.abs(this.getyPosition() - endNode.getyPosition()))
                        * BASICMOVEMENTCOST
        );
    }
}