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

    static Map<Shape, Object> shapeProperties = [
            (Shape.RECT)        : [
                    fileName: null,
                    scale   : null,
                    image   : null
            ],
            (Shape.CIRCLE)      : [
                    fileName: null,
                    scale   : null,
                    image   : null
            ],
            (Shape.LINE)      : [
                    fileName: null,
                    scale   : null,
                    image   : null
            ],
            (Shape.TREE)        : [
                    fileName: 'icons8-large-tree-48.png',
                    scale   : Main.TREE_SCALE,
                    image   : null
            ],
            (Shape.ROCK)        : [
                    fileName: 'icons8-silver-ore-48.png',
                    scale   : Main.ROCK_SCALE,
                    image   : null
            ],
            (Shape.WOOD)        : [
                    fileName: 'flaticon-firewood-64.png',
                    scale   : Main.SCALE_64,
                    image   : null
            ],
            (Shape.STONE)       : [
                    fileName: 'flaticon-rocks-64.png',
                    scale   : Main.SCALE_64,
                    image   : null
            ],
            (Shape.WARRIOR)     : [
                    fileName: 'icons8-iron-age-warrior-48.png',
                    scale   : Main.SCALE_48,
                    image   : null
            ],
            (Shape.SHAMAN)      : [
                    fileName: 'icons8-spartan-helmet-48.png',
                    scale   : Main.SCALE_48,
                    image   : null
            ],
            (Shape.SHAMAN_CAMP) : [
                    fileName: 'icons8-campfire-48.png',
                    scale   : Main.SCALE_48,
                    image   : null
            ],
            (Shape.SHAMAN_BUILD): [
                    fileName: 'flaticon-hammer-64.png',
                    scale   : Main.SCALE_64,
                    image   : null
            ],
            (Shape.FOLLOWER)    : [
                    fileName: 'icons8-sword-48.png',
                    scale   : Main.SCALE_48,
                    image   : null
            ],
            (Shape.FOLLOWER_BUILDER)    : [
                    fileName: 'flaticon-builder-hammer-64.png',
                    scale   : Main.SCALE_64,
                    image   : null
            ],
            (Shape.HUT)    : [
                    fileName: 'flaticon-hut-128.png',
                    scale   : Main.SCALE_x2_128,
                    image   : null
            ]
    ]

    static def NO_VILLAGERS = 1000

    static def init(def keyboard, def mouse) {
        Model.keyboard = keyboard
        Model.mouse = mouse
        tileNetwork = BackgroundUtils.generateBackground()
        backgroundImage = ImageUtils.createBGImage(tileNetwork)
        shapeProperties[Shape.TREE].image = ImageUtils.createImage(Shape.TREE)
        shapeProperties[Shape.ROCK].image = ImageUtils.createImage(Shape.ROCK)
        shapeProperties[Shape.STONE].image = ImageUtils.createImage(Shape.STONE)
        shapeProperties[Shape.WOOD].image = ImageUtils.createImage(Shape.WOOD)
        shapeProperties[Shape.WARRIOR].image = ImageUtils.createImage(Shape.WARRIOR)
        shapeProperties[Shape.SHAMAN].image = ImageUtils.createImage(Shape.SHAMAN)
        shapeProperties[Shape.SHAMAN_CAMP].image = ImageUtils.createImage(Shape.SHAMAN_CAMP)
        shapeProperties[Shape.SHAMAN_BUILD].image = ImageUtils.createImage(Shape.SHAMAN_BUILD)
        shapeProperties[Shape.FOLLOWER].image = ImageUtils.createImage(Shape.FOLLOWER)
        shapeProperties[Shape.FOLLOWER_BUILDER].image = ImageUtils.createImage(Shape.FOLLOWER_BUILDER)
        shapeProperties[Shape.HUT].image = ImageUtils.createImage(Shape.HUT)
        NO_VILLAGERS.times { villagers << Villager.test() }

        Model.villagers = villagers

        BackgroundUtils.setNaturalResources(tileNetwork)
    }
}
