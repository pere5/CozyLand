package main

import main.things.Drawable

class Node extends Drawable {
    int[][] neighborCosts = new int[3][3]
    int height = 0
    boolean accessible = true
    int movementCost
}
