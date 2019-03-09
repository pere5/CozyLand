package main.aStar

import javaSrc.AbstractNode
import javaSrc.NodeFactory

class StarNodeFactory implements NodeFactory {

    @Override
    AbstractNode createNode(int x, int y) {
        return new StarNode(x, y)
    }
}
