package main

import javaSrc.circulararray.CircularArrayList
import main.input.MyKeyboardListener
import main.input.MyMouseListener
import main.model.Tile
import main.model.Villager
import main.things.Drawable
import main.things.Drawable.Shape
import main.things.naturalResource.NaturalResource
import main.things.naturalResource.Rock
import main.things.naturalResource.Tree
import main.utility.BackgroundUtils
import main.utility.ImageUtils
import main.utility.ProbabilityUtils

import java.awt.image.BufferedImage
import java.util.concurrent.ConcurrentLinkedQueue

class Model {

    static int idGenerator = 0

    static int getNewId() {
        idGenerator++
    }

    enum TravelType {
        WATER, BEACH, FOREST, HILL, MOUNTAIN, PLAIN, ROAD, UP_HILL, DOWN_HILL, EVEN
    }

    static Map<Shape, Map> buildingResources = [
            (Shape.HUT): [
                    (Shape.WOOD): 2,
                    (Shape.STONE): 1
            ],
            (Shape.SCROLL): [
                    (Shape.WOOD): 3,
                    (Shape.STONE): 4
            ]
    ]

    static Map<TravelType, Map<Class<? extends NaturalResource>, Integer>> travelTypeNaturalResources = [
            (TravelType.WATER)   : [:],
            (TravelType.BEACH)   : [:],
            (TravelType.FOREST)  : [(Tree.class): 75],
            (TravelType.HILL)    : [(Tree.class): 75],
            (TravelType.MOUNTAIN): [(Rock.class): 75],
            (TravelType.PLAIN)   : [:],
            (TravelType.ROAD)    : [:],
    ]

    static def travelModifier = [
            (TravelType.WATER)    : 0.7d,
            (TravelType.BEACH)    : 1.3d,
            (TravelType.FOREST)   : 1.0d,
            (TravelType.HILL)     : 1.2d,
            (TravelType.MOUNTAIN) : 1.3d,
            (TravelType.PLAIN)    : 0.8d,
            (TravelType.ROAD)     : 0.4d,

            (TravelType.UP_HILL)  : 2.5d,
            (TravelType.EVEN)     : 1.0d,
            (TravelType.DOWN_HILL): 0.6d,
    ]


    static List<int[]> circularTileList = [[-1, 1], [0, 1], [1, 1], [1, 0], [1, -1], [0, -1], [-1, -1], [-1, 0] ] as CircularArrayList<int[]>

    static def tileDegrees = [
            [113, 157]: [-1,  1], [68, 112] : [0,  1], [23,   67]: [1,  1],
            [158, 202]: [-1,  0],                      [338,  22]: [1,  0],
            [203, 247]: [-1, -1], [248, 292]: [0, -1], [293, 337]: [1, -1]
    ]

    static MyKeyboardListener keyboard
    static MyMouseListener mouse
    static Tile[][] tileNetwork
    static boolean pause = false
    static List<Villager> villagers = []
    static ConcurrentLinkedQueue<Drawable> drawables = []
    static List<Map> frameSlots = []
    static def tileProbabilitiesForDegrees = ProbabilityUtils.calculateProbabilitiesModel()
    static BufferedImage backgroundImage

    static Map<Shape, BufferedImage> shapeImageMap = [
            (Shape.RECT)          : null,
            (Shape.CIRCLE)        : null,
            (Shape.LINE)          : null,
            (Shape.WOOD)          : ImageUtils.createImage('flaticon/flaticon-firewood-64.png', Main.SCALE_64),
            (Shape.STONE)         : ImageUtils.createImage('flaticon/flaticon-rocks-64.png', Main.SCALE_64),
            (Shape.HAMMER)        : ImageUtils.createImage('flaticon/flaticon-builder-hammer-64.png', Main.SCALE_64),
            (Shape.HAMMER_2)      : ImageUtils.createImage('flaticon/flaticon-hammer-64.png', Main.SCALE_64),
            (Shape.HUT)           : ImageUtils.createImage('flaticon/flaticon-hut-128.png', Main.SCALE_128),
            (Shape.SCROLL)        : ImageUtils.createImage('flaticon/flaticon-history-64.png', Main.SCALE_128),
            (Shape.SHIELD)        : ImageUtils.createImage('flaticon/flaticon-shield-64.png', Main.SCALE_128),
            (Shape.CARAVEL)       : ImageUtils.createImage('flaticon/flaticon-caravel-128.png', Main.SCALE_128),
            (Shape.WARRIOR)       : ImageUtils.createImage('icons8/icons8-iron-age-warrior-48.png', Main.SCALE_48),
            (Shape.SPARTAN_HELMET): ImageUtils.createImage('icons8/icons8-spartan-helmet-48.png', Main.SCALE_48),
            (Shape.CAMPFIRE)      : ImageUtils.createImage('icons8/icons8-campfire-48.png', Main.SCALE_48),
            (Shape.TREE)          : ImageUtils.createImage('icons8/icons8-large-tree-48.png', Main.TREE_SCALE),
            (Shape.SILVER_ORDE)   : ImageUtils.createImage('icons8/icons8-silver-ore-48.png', Main.ROCK_SCALE),
            (Shape.SWORD)         : ImageUtils.createImage('icons8/icons8-sword-48.png', Main.SCALE_48),
    ]

    static def NO_VILLAGERS = 1400

    static def init(def keyboard, def mouse) {
        Model.keyboard = keyboard
        Model.mouse = mouse
        tileNetwork = BackgroundUtils.generateBackground()
        backgroundImage = ImageUtils.createBGImage(tileNetwork)
        NO_VILLAGERS.times { villagers << Villager.test() }

        Model.villagers = villagers

        BackgroundUtils.setNaturalResources(tileNetwork)
    }
}
