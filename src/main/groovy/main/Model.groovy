package main

import javaSrc.circulararray.CircularArrayList
import main.input.MyKeyboardListener
import main.input.MyMouseListener
import main.model.Tile
import main.model.Villager
import main.things.Drawable
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

    enum Shape {
        RECT,
        CIRCLE,
        LINE,
        TREE,
        STONE,
        WOOD,
        SILVER_ORE,
        SPARTAN_HELMET,
        CAMPFIRE,
        HAMMER_2,
        WARRIOR,
        SWORD,
        HAMMER,
        HUT,
        SCROLL,
        SHIELD,
        CARAVEL,
        BOW,
        AXE,
        BAG,
        GEMS,
        SCEPTRE,
        SHIELD_2,
        SHOVEL,
        WOOD_2,
        SWORD_2,
        SWORD_3,
        CABIN,
        FIREWOOD,
        HUT_5,
        HUT_4,
        HUT_3,
        HUT_2,
        LOG,
        MILL,
        TEEPEE,
        WOODEN_HOUSE,
        BEAR,
        TOUCAN,
        REINDEER,
        MOOSE_2,
        GOLDEN_EAGLE,
        EAGLE_2,
        EAGLE,
        DEER,
        BIRD,
        BIRD_2,
        BEAR_2,
        BONFIRE_2,
        BONFIRE,
        CAMPING,
        BUSH,
        CRUCIBLE,
        GEM,
        AFRICAN,
        BOUDICCA,
        BRENNUS,
        HAWAIIAN_2,
        HAWAIIAN,
        QU_YUAN,
        SPEAR,
        TRIBAL,
        VERCINGETORIX,
        WARRIOR_3,
        WARRIOR_2,
        WU_ZIXU,
        OAK,
        OAK_2,
        PINE_2,
        PINE_3,
        PINE_4,
        PINE,
        STONE_2,
        CAMPING_TENT,
        TEEPEE_2,
        TEEPEE_3,
        TEEPEE_4,
        MOOSE,
        TENT,
        DUBLIN_CASTLE,
        DUNGEON_1,
        DUNGEON,
        EMBLEM,
        FORTRESS,
        GALLEON,
        GATE,
        HOUSE,
        CARRIAGE,
        CASTLE_1,
        CASTLE_2,
        CASTLE_3,
        CASTLE,
        CATAPULT_1,
        CATAPULT_2,
        CATAPULT,
        CHARIOT,
        COIN,
        BEER,
        BEER_MUG,
        BELL_TOWER,
        CABAIN,
        CAR,
        CARAVEL_1,
        CARAVEL_2,
        MEDIEVAL_1,
        MEDIEVAL_2,
        MEDIEVAL_3,
        MEDIEVAL_4,
        MEDIEVAL_5,
        MEDIEVAL_6,
        MEDIEVAL,
        MISTREL,
        MISSIVE,
        MONEY_BAG,
        IRISH_ROUND_TOWER_1,
        IRISH_ROUND_TOWER_2,
        IRISH_ROUND_TOWER,
        JUGGLER,
        KNIGHT_1,
        KNIGHT_2,
        KNIGHT_3,
        KNIGHT_4,
        KNIGHT_5,
        KNIGHT_6,
        KNIGHT,
        LOATHLY_LADY_1,
        LOATHLY_LADY,
        WARRIOR_7,
        WARRIOR_6,
        WARRIOR_5,
        WARRIOR_4,
        WIZARD,
        TENT_1,
        TORCH,
        TOWER_1,
        TOWER_2,
        TOWER_3,
        TOWER_4,
        TOWER_5,
        TOWER,
        TRUMPET,
        WALLS_OF_AVILA_1,
        WALLS_OF_AVILA,
        NUN,
        PILLORY,
        PRIEST_1,
        PRIEST_2,
        PRIEST_3,
        PRIEST,
        RABBI,
        STRECHER,
        SWORD_1,
        TEACHER
    }

    static Map<Shape, BufferedImage> shapeImageMap = [
            (Shape.RECT)               : null,
            (Shape.CIRCLE)             : null,
            (Shape.LINE)               : null,
            (Shape.WOOD)               : ImageUtils.createImage('flaticon/flaticon-firewood-64.png', Main.SCALE_64),
            (Shape.BOW)                : ImageUtils.createImage('flaticon/flaticon-arch-64.png', Main.SCALE_64),
            (Shape.AXE)                : ImageUtils.createImage('flaticon/flaticon-axe-64.png', Main.SCALE_64),
            (Shape.BAG)                : ImageUtils.createImage('flaticon/flaticon-bag-64.png', Main.SCALE_64),
            (Shape.GEMS)               : ImageUtils.createImage('flaticon/flaticon-gems-64.png', Main.SCALE_64),
            (Shape.SCEPTRE)            : ImageUtils.createImage('flaticon/flaticon-sceptre-64.png', Main.SCALE_64),
            (Shape.SHIELD_2)           : ImageUtils.createImage('flaticon/flaticon-shield-2-64.png', Main.SCALE_64),
            (Shape.SHOVEL)             : ImageUtils.createImage('flaticon/flaticon-shovel-64.png', Main.SCALE_64),
            (Shape.WOOD_2)             : ImageUtils.createImage('flaticon/flaticon-wood-64.png', Main.SCALE_64),
            (Shape.SWORD_2)            : ImageUtils.createImage('flaticon/flaticon-swords-64.png', Main.SCALE_64),
            (Shape.SWORD_3)            : ImageUtils.createImage('flaticon/flaticon-sword-64.png', Main.SCALE_64),
            (Shape.STONE)              : ImageUtils.createImage('flaticon/flaticon-rocks-64.png', Main.SCALE_64),
            (Shape.HAMMER)             : ImageUtils.createImage('flaticon/flaticon-builder-hammer-64.png', Main.SCALE_64),
            (Shape.BEAR)               : ImageUtils.createImage('flaticon/flaticon-bear-64.png', Main.SCALE_64),
            (Shape.BONFIRE_2)          : ImageUtils.createImage('flaticon/flaticon-bonfire-2-64.png', Main.SCALE_64),
            (Shape.BONFIRE)            : ImageUtils.createImage('flaticon/flaticon-bonfire-64.png', Main.SCALE_64),
            (Shape.CAMPING)            : ImageUtils.createImage('flaticon/flaticon-camping-64.png', Main.SCALE_64),
            (Shape.CAMPING_TENT)       : ImageUtils.createImage('flaticon/flaticon-camping-tent-64.png', Main.SCALE_64),
            (Shape.TEEPEE_2)           : ImageUtils.createImage('flaticon/flaticon-teepee-2-64.png', Main.SCALE_64),
            (Shape.TEEPEE_3)           : ImageUtils.createImage('flaticon/flaticon-teepee-3-64.png', Main.SCALE_64),
            (Shape.TEEPEE_4)           : ImageUtils.createImage('flaticon/flaticon-teepee-4-64.png', Main.SCALE_64),
            (Shape.MOOSE)              : ImageUtils.createImage('flaticon/flaticon-moose-64.png', Main.SCALE_64),
            (Shape.BEAR_2)             : ImageUtils.createImage('flaticon/flaticon-bear-2-64.png', Main.SCALE_64),
            (Shape.BIRD_2)             : ImageUtils.createImage('flaticon/flaticon-bird-2-64.png', Main.SCALE_64),
            (Shape.BIRD)               : ImageUtils.createImage('flaticon/flaticon-bird-64.png', Main.SCALE_64),
            (Shape.CARAVEL_2)          : ImageUtils.createImage('flaticon/flaticon-caravel-2-64.png', Main.SCALE_64),
            (Shape.CARAVEL_1)          : ImageUtils.createImage('flaticon/flaticon-caravel-1-64.png', Main.SCALE_64),
            (Shape.CAR)                : ImageUtils.createImage('flaticon/flaticon-car-64.png', Main.SCALE_64),
            (Shape.CABAIN)             : ImageUtils.createImage('flaticon/flaticon-cabain-64.png', Main.SCALE_64),
            (Shape.BELL_TOWER)         : ImageUtils.createImage('flaticon/flaticon-bell-tower-64.png', Main.SCALE_64),
            (Shape.BEER_MUG)           : ImageUtils.createImage('flaticon/flaticon-beer-mug-64.png', Main.SCALE_64),
            (Shape.BEER)               : ImageUtils.createImage('flaticon/flaticon-beer-64.png', Main.SCALE_64),
            (Shape.COIN)               : ImageUtils.createImage('flaticon/flaticon-coin-64.png', Main.SCALE_64),
            (Shape.CHARIOT)            : ImageUtils.createImage('flaticon/flaticon-chariot-64.png', Main.SCALE_64),
            (Shape.CATAPULT)           : ImageUtils.createImage('flaticon/flaticon-catapult-64.png', Main.SCALE_64),
            (Shape.CATAPULT_2)         : ImageUtils.createImage('flaticon/flaticon-catapult-2-64.png', Main.SCALE_64),
            (Shape.CATAPULT_1)         : ImageUtils.createImage('flaticon/flaticon-catapult-1-64.png', Main.SCALE_64),
            (Shape.CASTLE)             : ImageUtils.createImage('flaticon/flaticon-castle-64.png', Main.SCALE_64),
            (Shape.CASTLE_3)           : ImageUtils.createImage('flaticon/flaticon-castle-3-64.png', Main.SCALE_64),
            (Shape.CASTLE_2)           : ImageUtils.createImage('flaticon/flaticon-castle-2-64.png', Main.SCALE_64),
            (Shape.CASTLE_1)           : ImageUtils.createImage('flaticon/flaticon-castle-1-64.png', Main.SCALE_64),
            (Shape.CARRIAGE)           : ImageUtils.createImage('flaticon/flaticon-carriage-64.png', Main.SCALE_64),
            (Shape.DEER)               : ImageUtils.createImage('flaticon/flaticon-deer-64.png', Main.SCALE_64),
            (Shape.EAGLE_2)            : ImageUtils.createImage('flaticon/flaticon-eagle-2-64.png', Main.SCALE_64),
            (Shape.HOUSE)              : ImageUtils.createImage('flaticon/flaticon-house-64.png', Main.SCALE_64),
            (Shape.GATE)               : ImageUtils.createImage('flaticon/flaticon-gate-64.png', Main.SCALE_64),
            (Shape.GALLEON)            : ImageUtils.createImage('flaticon/flaticon-galleon-64.png', Main.SCALE_64),
            (Shape.FORTRESS)           : ImageUtils.createImage('flaticon/flaticon-fortress-64.png', Main.SCALE_64),
            (Shape.EMBLEM)             : ImageUtils.createImage('flaticon/flaticon-emblem-64.png', Main.SCALE_64),
            (Shape.DUNGEON)            : ImageUtils.createImage('flaticon/flaticon-dungeon-64.png', Main.SCALE_64),
            (Shape.DUNGEON_1)          : ImageUtils.createImage('flaticon/flaticon-dungeon-1-64.png', Main.SCALE_64),
            (Shape.DUBLIN_CASTLE)      : ImageUtils.createImage('flaticon/flaticon-dublin-castle-64.png', Main.SCALE_64),
            (Shape.EAGLE)              : ImageUtils.createImage('flaticon/flaticon-eagle-64.png', Main.SCALE_64),
            (Shape.GOLDEN_EAGLE)       : ImageUtils.createImage('flaticon/flaticon-golden-eagle-64.png', Main.SCALE_64),
            (Shape.LOATHLY_LADY)       : ImageUtils.createImage('flaticon/flaticon-loathly-lady-64.png', Main.SCALE_64),
            (Shape.LOATHLY_LADY_1)     : ImageUtils.createImage('flaticon/flaticon-loathly-lady-1-64.png', Main.SCALE_64),
            (Shape.KNIGHT)             : ImageUtils.createImage('flaticon/flaticon-knight-64.png', Main.SCALE_64),
            (Shape.KNIGHT_6)           : ImageUtils.createImage('flaticon/flaticon-knight-6-64.png', Main.SCALE_64),
            (Shape.KNIGHT_5)           : ImageUtils.createImage('flaticon/flaticon-knight-5-64.png', Main.SCALE_64),
            (Shape.KNIGHT_4)           : ImageUtils.createImage('flaticon/flaticon-knight-4-64.png', Main.SCALE_64),
            (Shape.KNIGHT_3)           : ImageUtils.createImage('flaticon/flaticon-knight-3-64.png', Main.SCALE_64),
            (Shape.KNIGHT_2)           : ImageUtils.createImage('flaticon/flaticon-knight-2-64.png', Main.SCALE_64),
            (Shape.KNIGHT_1)           : ImageUtils.createImage('flaticon/flaticon-knight-1-64.png', Main.SCALE_64),
            (Shape.JUGGLER)            : ImageUtils.createImage('flaticon/flaticon-juggler-64.png', Main.SCALE_64),
            (Shape.IRISH_ROUND_TOWER)  : ImageUtils.createImage('flaticon/flaticon-irish-round-tower-64.png', Main.SCALE_64),
            (Shape.IRISH_ROUND_TOWER_2): ImageUtils.createImage('flaticon/flaticon-irish-round-tower-2-64.png', Main.SCALE_64),
            (Shape.IRISH_ROUND_TOWER_1): ImageUtils.createImage('flaticon/flaticon-irish-round-tower-1-64.png', Main.SCALE_64),
            (Shape.MOOSE_2)            : ImageUtils.createImage('flaticon/flaticon-moose-2.png', Main.SCALE_64),
            (Shape.REINDEER)           : ImageUtils.createImage('flaticon/flaticon-reindeer-64.png', Main.SCALE_64),
            (Shape.MONEY_BAG)          : ImageUtils.createImage('flaticon/flaticon-money-bag-64.png', Main.SCALE_64),
            (Shape.MISSIVE)            : ImageUtils.createImage('flaticon/flaticon-missive-64.png', Main.SCALE_64),
            (Shape.MISTREL)            : ImageUtils.createImage('flaticon/flaticon-minstrel-64.png', Main.SCALE_64),
            (Shape.MEDIEVAL)           : ImageUtils.createImage('flaticon/flaticon-medieval-64.png', Main.SCALE_64),
            (Shape.MEDIEVAL_6)         : ImageUtils.createImage('flaticon/flaticon-medieval-6-64.png', Main.SCALE_64),
            (Shape.MEDIEVAL_5)         : ImageUtils.createImage('flaticon/flaticon-medieval-5-64.png', Main.SCALE_64),
            (Shape.MEDIEVAL_4)         : ImageUtils.createImage('flaticon/flaticon-medieval-4-64.png', Main.SCALE_64),
            (Shape.MEDIEVAL_3)         : ImageUtils.createImage('flaticon/flaticon-medieval-3-64.png', Main.SCALE_64),
            (Shape.MEDIEVAL_2)         : ImageUtils.createImage('flaticon/flaticon-medieval-2-64.png', Main.SCALE_64),
            (Shape.MEDIEVAL_1)         : ImageUtils.createImage('flaticon/flaticon-medieval-1-64.png', Main.SCALE_64),
            (Shape.TOUCAN)             : ImageUtils.createImage('flaticon/flaticon-toucan-64.png', Main.SCALE_64),
            (Shape.TENT)               : ImageUtils.createImage('flaticon/flaticon-tent-64.png', Main.SCALE_64),
            (Shape.HAMMER_2)           : ImageUtils.createImage('flaticon/flaticon-hammer-64.png', Main.SCALE_64),
            (Shape.TEACHER)            : ImageUtils.createImage('flaticon/flaticon-teacher-64.png', Main.SCALE_64),
            (Shape.SWORD_1)            : ImageUtils.createImage('flaticon/flaticon-sword-1-64.png', Main.SCALE_64),
            (Shape.STRECHER)           : ImageUtils.createImage('flaticon/flaticon-stretcher-64.png', Main.SCALE_64),
            (Shape.RABBI)              : ImageUtils.createImage('flaticon/flaticon-rabbi-64.png', Main.SCALE_64),
            (Shape.PRIEST)             : ImageUtils.createImage('flaticon/flaticon-priest-64.png', Main.SCALE_64),
            (Shape.PRIEST_3)           : ImageUtils.createImage('flaticon/flaticon-priest-3-64.png', Main.SCALE_64),
            (Shape.PRIEST_2)           : ImageUtils.createImage('flaticon/flaticon-priest-2-64.png', Main.SCALE_64),
            (Shape.PRIEST_1)           : ImageUtils.createImage('flaticon/flaticon-priest-1-64.png', Main.SCALE_64),
            (Shape.PILLORY)            : ImageUtils.createImage('flaticon/flaticon-pillory-64.png', Main.SCALE_64),
            (Shape.NUN)                : ImageUtils.createImage('flaticon/flaticon-nun-64.png', Main.SCALE_64),
            (Shape.WOODEN_HOUSE)       : ImageUtils.createImage('flaticon/flaticon-wooden-house-64.png', Main.SCALE_64),
            (Shape.TEEPEE)             : ImageUtils.createImage('flaticon/flaticon-teepee-64.png', Main.SCALE_64),
            (Shape.WALLS_OF_AVILA)     : ImageUtils.createImage('flaticon/flaticon-walls-of-avila-64.png', Main.SCALE_64),
            (Shape.WALLS_OF_AVILA_1)   : ImageUtils.createImage('flaticon/flaticon-walls-of-avila-1-64.png', Main.SCALE_64),
            (Shape.TRUMPET)            : ImageUtils.createImage('flaticon/flaticon-trumpet-64.png', Main.SCALE_64),
            (Shape.TOWER)              : ImageUtils.createImage('flaticon/flaticon-tower-64.png', Main.SCALE_64),
            (Shape.TOWER_5)            : ImageUtils.createImage('flaticon/flaticon-tower-5-64.png', Main.SCALE_64),
            (Shape.TOWER_4)            : ImageUtils.createImage('flaticon/flaticon-tower-4-64.png', Main.SCALE_64),
            (Shape.TOWER_3)            : ImageUtils.createImage('flaticon/flaticon-tower-3-64.png', Main.SCALE_64),
            (Shape.TOWER_2)            : ImageUtils.createImage('flaticon/flaticon-tower-2-64.png', Main.SCALE_64),
            (Shape.TOWER_1)            : ImageUtils.createImage('flaticon/flaticon-tower-1-64.png', Main.SCALE_64),
            (Shape.TORCH)              : ImageUtils.createImage('flaticon/flaticon-torch-64.png', Main.SCALE_64),
            (Shape.TENT_1)             : ImageUtils.createImage('flaticon/flaticon-tent-1-64.png', Main.SCALE_64),
            (Shape.MILL)               : ImageUtils.createImage('flaticon/flaticon-mill-64.png', Main.SCALE_64),
            (Shape.WU_ZIXU)            : ImageUtils.createImage('flaticon/flaticon-wu-zixu-64.png', Main.SCALE_64),
            (Shape.WIZARD)             : ImageUtils.createImage('flaticon/flaticon-wizard-64.png', Main.SCALE_64),
            (Shape.WARRIOR_4)          : ImageUtils.createImage('flaticon/flaticon-warrior-6-64.png', Main.SCALE_64),
            (Shape.WARRIOR_5)          : ImageUtils.createImage('flaticon/flaticon-warrior-5-64.png', Main.SCALE_64),
            (Shape.WARRIOR_6)          : ImageUtils.createImage('flaticon/flaticon-warrior-4-64.png', Main.SCALE_64),
            (Shape.WARRIOR_7)          : ImageUtils.createImage('flaticon/flaticon-warrior-3-64.png', Main.SCALE_64),
            (Shape.WARRIOR_2)          : ImageUtils.createImage('flaticon/flaticon-warrior-64.png', Main.SCALE_64),
            (Shape.WARRIOR_3)          : ImageUtils.createImage('flaticon/flaticon-warrior-2-64.png', Main.SCALE_64),
            (Shape.VERCINGETORIX)      : ImageUtils.createImage('flaticon/flaticon-vercingetorix-64.png', Main.SCALE_64),
            (Shape.TRIBAL)             : ImageUtils.createImage('flaticon/flaticon-tribal-64.png', Main.SCALE_64),
            (Shape.SPEAR)              : ImageUtils.createImage('flaticon/flaticon-spear-64.png', Main.SCALE_64),
            (Shape.QU_YUAN)            : ImageUtils.createImage('flaticon/flaticon-qu-yuan-64.png', Main.SCALE_64),
            (Shape.HAWAIIAN)           : ImageUtils.createImage('flaticon/flaticon-hawaiian-64.png', Main.SCALE_64),
            (Shape.HAWAIIAN_2)         : ImageUtils.createImage('flaticon/flaticon-hawaiian-2-64.png', Main.SCALE_64),
            (Shape.BRENNUS)            : ImageUtils.createImage('flaticon/flaticon-brennus-64.png', Main.SCALE_64),
            (Shape.BOUDICCA)           : ImageUtils.createImage('flaticon/flaticon-boudicca-64.png', Main.SCALE_64),
            (Shape.AFRICAN)            : ImageUtils.createImage('flaticon/flaticon-african-64.png', Main.SCALE_64),
            (Shape.STONE_2)            : ImageUtils.createImage('flaticon/flaticon-stone-64.png', Main.SCALE_64),
            (Shape.PINE)               : ImageUtils.createImage('flaticon/flaticon-pine-64.png', Main.SCALE_64),
            (Shape.PINE_4)             : ImageUtils.createImage('flaticon/flaticon-pine-4-64.png', Main.SCALE_64),
            (Shape.PINE_3)             : ImageUtils.createImage('flaticon/flaticon-pine-3-64.png', Main.SCALE_64),
            (Shape.PINE_2)             : ImageUtils.createImage('flaticon/flaticon-pine-2-64.png', Main.SCALE_64),
            (Shape.OAK_2)              : ImageUtils.createImage('flaticon/flaticon-oak-2.png', Main.SCALE_64),
            (Shape.OAK)                : ImageUtils.createImage('flaticon/flaticon-oak-64.png', Main.SCALE_64),
            (Shape.GEM)                : ImageUtils.createImage('flaticon/flaticon-gem-64.png', Main.SCALE_64),
            (Shape.CRUCIBLE)           : ImageUtils.createImage('flaticon/flaticon-crucible-64.png', Main.SCALE_64),
            (Shape.BUSH)               : ImageUtils.createImage('flaticon/flaticon-bush-64.png', Main.SCALE_64),
            (Shape.LOG)                : ImageUtils.createImage('flaticon/flaticon-log-64.png', Main.SCALE_64),
            (Shape.HUT_2)              : ImageUtils.createImage('flaticon/flaticon-hut-64.png', Main.SCALE_64),
            (Shape.HUT_3)              : ImageUtils.createImage('flaticon/flaticon-hut-2-64.png', Main.SCALE_64),
            (Shape.HUT_4)              : ImageUtils.createImage('flaticon/flaticon-hut-3-64.png', Main.SCALE_64),
            (Shape.HUT_5)              : ImageUtils.createImage('flaticon/flaticon-hut-4-64.png', Main.SCALE_64),
            (Shape.FIREWOOD)           : ImageUtils.createImage('flaticon/flaticon-firewood-2-64.png', Main.SCALE_64),
            (Shape.CABIN)              : ImageUtils.createImage('flaticon/flaticon-cabin-64.png', Main.SCALE_64),
            (Shape.HUT)                : ImageUtils.createImage('flaticon/flaticon-hut-128.png', Main.SCALE_128),
            (Shape.SCROLL)             : ImageUtils.createImage('flaticon/flaticon-history-64.png', Main.SCALE_128),
            (Shape.SHIELD)             : ImageUtils.createImage('flaticon/flaticon-shield-64.png', Main.SCALE_128),
            (Shape.CARAVEL)            : ImageUtils.createImage('flaticon/flaticon-caravel-128.png', Main.SCALE_128),
            (Shape.WARRIOR)            : ImageUtils.createImage('icons8/icons8-iron-age-warrior-48.png', Main.SCALE_48),
            (Shape.SPARTAN_HELMET)     : ImageUtils.createImage('icons8/icons8-spartan-helmet-48.png', Main.SCALE_48),
            (Shape.CAMPFIRE)           : ImageUtils.createImage('icons8/icons8-campfire-48.png', Main.SCALE_48),
            (Shape.TREE)               : ImageUtils.createImage('icons8/icons8-large-tree-48.png', Main.TREE_SCALE),
            (Shape.SILVER_ORE)         : ImageUtils.createImage('icons8/icons8-silver-ore-48.png', Main.ROCK_SCALE),
            (Shape.SWORD)              : ImageUtils.createImage('icons8/icons8-sword-48.png', Main.SCALE_48),
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
