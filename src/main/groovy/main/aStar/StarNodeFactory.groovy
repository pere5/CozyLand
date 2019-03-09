package main.aStar

import javaSrc.AbstractNode
import javaSrc.ExampleNode
import javaSrc.NodeFactory

class StarNodeFactory implements NodeFactory {

    @Override
    AbstractNode createNode(int x, int y) {
        return new Node(x, y);
    }
}
