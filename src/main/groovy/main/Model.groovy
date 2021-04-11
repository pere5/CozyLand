package main

import javaSrc.circulararray.CircularArrayList
import main.input.MyKeyboardListener
import main.input.MyMouseListener
import main.model.Tile
import main.model.Villager
import main.things.Drawable
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
            (Shape.TEMPLE): [
                    (Shape.WOOD): 3,
                    (Shape.STONE): 4
            ]
    ]

    static Map<TravelType, Map> travelTypeNaturalResources = [
            (TravelType.WATER)   : [:],
            (TravelType.BEACH)   : [clazz: Tree.class, shape: Shape.TREE_AUTUMN_LEAF, prevalence: 110, shade: 0.9],
            (TravelType.FOREST)  : [clazz: Tree.class, shape: Shape.TREE_LEAF, prevalence: 80, shade: 0.9],
            (TravelType.HILL)    : [clazz: Tree.class, shape: Shape.TREE_PINE, prevalence: 50, shade: 0.8],
            (TravelType.MOUNTAIN): [clazz: Rock.class, shape: Shape.ROCK, prevalence: 75, shade: 0.5],
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

    enum Shape {
        RECT,
        CIRCLE,
        LINE,
        WOOD,
        STONE,
        BEAR,
        BIRD,
        BOAT,
        BUSH,
        CAVE,
        DEER,
        ELK,
        FISH,
        FORT,
        HOUSE,
        HUT,
        ROCK,
        RUIN,
        TEMPLE,
        TENT,
        TOOL,
        TOTEM,
        TREE_LEAF,
        TREE_AUTUMN_LEAF,
        TREE_PINE,
        TREE_PALM,
        TREE_CACTUS,
        VILLAGER,
        WINDMILL,
        WARRIOR,
        SHAMAN,
        FISHER,
        BUILDER
    }

    static final Double SCALE = 0.9
    static final Double SCALE_64 = 0.20 * SCALE
    static final Double SCALE_TREE = 1.2 * SCALE
    static final Double SCALE_ROCK = 0.7 * SCALE
    static final Double SCALE_PEOPLE = 0.5 * SCALE
    static final Double SCALE_TOTEM = 0.9 * SCALE
    static final Double SCALE_HUT = 0.8 * SCALE

    static Map<Shape, List<BufferedImage>> shapeImageMap = [
            (Shape.RECT)            : null,
            (Shape.CIRCLE)          : null,
            (Shape.LINE)            : null,
            (Shape.ROCK)            : ImageUtils.readFromDir('icons/ROCK', SCALE_ROCK),
            (Shape.TREE_LEAF)       : ImageUtils.readFromDir('icons/TREE/LEAF', SCALE_TREE),
            (Shape.TREE_AUTUMN_LEAF): ImageUtils.readFromDir('icons/TREE/AUTUMN_LEAF', SCALE_TREE),
            (Shape.TREE_PINE)       : ImageUtils.readFromDir('icons/TREE/PINE', SCALE_TREE),
            (Shape.TREE_PALM)       : ImageUtils.readFromDir('icons/TREE/PALM', SCALE_TREE),
            (Shape.TREE_CACTUS)     : ImageUtils.readFromDir('icons/TREE/CACTUS', SCALE_TREE),
            (Shape.BEAR)            : ImageUtils.readFromDir('icons/BEAR', SCALE_64),
            (Shape.BIRD)            : ImageUtils.readFromDir('icons/BIRD', SCALE_64),
            (Shape.BOAT)            : ImageUtils.readFromDir('icons/BOAT', SCALE_64),
            (Shape.BUSH)            : ImageUtils.readFromDir('icons/BUSH', SCALE_64),
            (Shape.CAVE)            : ImageUtils.readFromDir('icons/CAVE', SCALE_64),
            (Shape.DEER)            : ImageUtils.readFromDir('icons/DEER', SCALE_64),
            (Shape.ELK)             : ImageUtils.readFromDir('icons/ELK', SCALE_64),
            (Shape.FISH)            : ImageUtils.readFromDir('icons/FISH', SCALE_64),
            (Shape.FORT)            : ImageUtils.readFromDir('icons/FORT', SCALE_64),
            (Shape.HOUSE)           : ImageUtils.readFromDir('icons/HOUSE', SCALE_64),
            (Shape.RUIN)            : ImageUtils.readFromDir('icons/RUIN', SCALE_64),
            (Shape.TEMPLE)          : ImageUtils.readFromDir('icons/TEMPLE', SCALE_64),
            (Shape.TENT)            : ImageUtils.readFromDir('icons/TENT', SCALE_64),
            (Shape.TOOL)            : ImageUtils.readFromDir('icons/TOOL', SCALE_64),
            (Shape.WINDMILL)        : ImageUtils.readFromDir('icons/WINDMILL', SCALE_64),
            (Shape.TOTEM)           : ImageUtils.readFromDir('icons/TOTEM', SCALE_TOTEM),
            (Shape.HUT)             : ImageUtils.readFromDir('icons/HUT', SCALE_HUT),
            (Shape.BUILDER)         : ImageUtils.readFromDir('icons/PEOPLE/BUILDER', SCALE_PEOPLE),
            (Shape.FISHER)          : ImageUtils.readFromDir('icons/PEOPLE/FISHER', SCALE_PEOPLE),
            (Shape.SHAMAN)          : ImageUtils.readFromDir('icons/PEOPLE/SHAMAN', SCALE_PEOPLE),
            (Shape.VILLAGER)        : ImageUtils.readFromDir('icons/PEOPLE/VILLAGER', SCALE_PEOPLE),
            (Shape.WARRIOR)         : ImageUtils.readFromDir('icons/PEOPLE/WARRIOR', SCALE_PEOPLE)
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
