package main

import javaSrc.circulararray.CircularArrayList
import main.calculator.Background
import main.calculator.Probabilities
import main.input.MyKeyboardListener
import main.input.MyMouseListener
import main.model.Tile
import main.rule.Affinity
import main.rule.Rule
import main.things.Drawable
import main.villager.Villager

import java.awt.*
import java.awt.geom.Point2D
import java.awt.image.BufferedImage
import java.util.List
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ThreadLocalRandom

class Model {

    static int idGenerator = 0

    static int getNewId() {
        idGenerator++
    }

    enum TravelType {
        WATER, FOREST, HILL, MOUNTAIN, PLAIN, ROAD, UP_HILL, DOWN_HILL, EVEN
    }

    static def travelModifier = [
            (TravelType.WATER)    : 0.7d,
            (TravelType.FOREST)   : 1.0d,
            (TravelType.HILL)     : 1.2d,
            (TravelType.MOUNTAIN) : 1.3d,
            (TravelType.PLAIN)    : 0.8d,
            (TravelType.ROAD)     : 0.4d,

            (TravelType.UP_HILL)  : 1.3d,
            (TravelType.EVEN)     : 1.0d,
            (TravelType.DOWN_HILL): 0.8d,
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
    static List<Integer> frameSlots = [0, 0, 0, 0, 0]
    static List<Rule> rules = generateRules()
    static def tileProbabilitiesForDegrees = Probabilities.calculateProbabilitiesModel()
    static BufferedImage backgroundImage

    static def init(def keyboard, def mouse) {
        Model.keyboard = keyboard
        Model.mouse = mouse
        tileNetwork = Background.generateBackground()
        backgroundImage = createBGImage()
        def villagers = [
                Villager.test(), Villager.test(), Villager.test(), Villager.test(), Villager.test(),
                Villager.test(), Villager.test(), Villager.test(), Villager.test(), Villager.test(),
                Villager.test(), Villager.test(), Villager.test(), Villager.test(), Villager.test()
        ]
        def stones = []
        def trees = []
        def artifacts = []

        def drawables = new ConcurrentLinkedQueue<Drawable>([
                artifacts, stones, trees, villagers
        ].flatten() as List<Drawable>)

        Model.villagers = villagers
        Model.drawables = drawables
    }

    static List<Rule> generateRules() {
        int rank = Integer.MAX_VALUE
        [new Affinity(rank: --rank)]
    }

    static BufferedImage createBGImage() {
        Tile[][] tileNetwork = tileNetwork
        BufferedImage image = new BufferedImage(
                tileNetwork.length * Main.TILE_WIDTH,
                tileNetwork[0].length * Main.TILE_WIDTH,
                BufferedImage.TYPE_INT_RGB
        )
        Graphics2D g2d = image.createGraphics()

        for (int x = 0; x < tileNetwork.length; x++) {
            for (int y = 0; y < tileNetwork[x].length; y++) {
                Drawable drawable = tileNetwork[x][y]
                g2d.setPaint(drawable.color)
                if (drawable.shape == Drawable.SHAPES.RECT) {
                    g2d.fillRect(drawable.x as int, drawable.y as int, drawable.size, drawable.size)
                }
            }
        }

        return image
    }

    static List<Color> gradient(Color color1, Color color2, int steps) {
        def colors = []

        for (int i = 0; i < steps; i++) {
            Double ratio = i / steps
            int r = color2.getRed() * ratio + color1.getRed() * (1 - ratio)
            int g = color2.getGreen() * ratio + color1.getGreen() * (1 - ratio)
            int b = color2.getBlue() * ratio + color1.getBlue() * (1 - ratio)
            colors << new Color(r, g, b)
        }
        colors
    }

    static int round(BigDecimal number) {
        if (number > 0) {
            return number + 0.5
        } else {
            return number - 0.5
        }
    }

    static int round(Double number) {
        if (number > 0) {
            return number + 0.5
        } else {
            return number - 0.5
        }
    }

    static int[] round(Double[] numbers) {
        numbers.collect { round(it) }
    }

    static int[] round(List<Double> numbers) {
        numbers.collect { round(it) }
    }

    static Double[] generateXY() {
        Double[] xy = [
                Main.MAP_WIDTH / 2 + generate((Main.MAP_WIDTH / 3) as int),
                Main.MAP_HEIGHT / 2 + generate((Main.MAP_HEIGHT / 3) as int)
        ]

        def tileXY = pixelToTileIdx(xy)

        def tile = Model.tileNetwork[tileXY[0]][tileXY[1]] as Tile

        if (tile.travelType == TravelType.WATER) {
            return generateXY()
        } else {
            return xy
        }
    }

    static int[] generateTileXY() {
        def tileNetwork = Model.tileNetwork as Tile[][]

        int[] tileXY = [
                tileNetwork.length / 2 + generate((tileNetwork.length / 3) as int),
                tileNetwork[0].length / 2 + generate((tileNetwork[0].length / 3) as int)
        ]

        def tile = Model.tileNetwork[tileXY[0]][tileXY[1]] as Tile

        if (tile.travelType == TravelType.WATER) {
            return generateTileXY()
        } else {
            return tileXY
        }
    }

    static Double generate(int distance) {
        return distance - ThreadLocalRandom.current().nextInt(0, distance * 2 + 1)
    }

    static int[] pixelToTileIdx(int[] pixels) {
        pixels.collect { it / Main.TILE_WIDTH }
    }

    static int[] pixelToTileIdx(Double[] pixels) {
        pixels.collect { it / Main.TILE_WIDTH }
    }

    static Double[] tileToPixelIdx(int[] tile) {
        tile.collect { (it * Main.TILE_WIDTH) }
    }

    static int[] tileToPixelIdx(List<Integer> tile) {
        tile.collect { (it * Main.TILE_WIDTH) }
    }

    static int calculateDegreeRound(int[] start, int[] dest) {
        Double deg = Math.toDegrees(Math.atan2(dest[1] - start[1], dest[0] - start[0]))
        Model.round(deg >= 0 ? deg : deg + 360)
    }

    static int distance(int[] a, int[] b) {
        Point2D.distance(a[0], a[1], b[0], b[1])
    }
}
