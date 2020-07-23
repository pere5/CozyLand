package main

import javaSrc.circulararray.CircularArrayList
import javaSrc.color.GaussianFilter
import main.calculator.Background
import main.calculator.Probabilities
import main.exception.PerIsBorkenException
import main.input.MyKeyboardListener
import main.input.MyMouseListener
import main.model.Tile
import main.model.Villager
import main.things.Drawable
import main.things.Drawable.Shape
import main.things.resource.Resource
import main.things.resource.Stone
import main.things.resource.Tree

import javax.imageio.ImageIO
import java.awt.*
import java.awt.geom.Point2D
import java.awt.image.BufferedImage
import java.awt.image.RescaleOp
import java.util.List
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ThreadLocalRandom

class Model {

    static int idGenerator = 0

    static int getNewId() {
        idGenerator++
    }

    enum TravelType {
        WATER, BEACH, FOREST, HILL, MOUNTAIN, PLAIN, ROAD, UP_HILL, DOWN_HILL, EVEN
    }

    static Map<TravelType, Map<Class<? extends Resource>, Integer>> travelTypeResources = [
            (TravelType.WATER)   : [:],
            (TravelType.BEACH)   : [:],
            (TravelType.FOREST)  : [(Tree.class): 40],
            (TravelType.HILL)    : [(Tree.class): 40],
            (TravelType.MOUNTAIN): [(Stone.class): 30],
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
    static def tileProbabilitiesForDegrees = Probabilities.calculateProbabilitiesModel()
    static BufferedImage backgroundImage

    static Map<Shape, Map<String, Object>> shapeProperties = [
            (Shape.RECT)       : [
                    fileName: null,
                    scale   : null,
                    image   : null,
                    offsetX : null,
                    offsetY : null
            ],
            (Shape.CIRCLE)     : [
                    fileName: null,
                    scale   : null,
                    image   : null,
                    offsetX : null,
                    offsetY : null
            ],(Shape.TREE)     : [
                    fileName: 'icons8-large-tree-48.png',
                    scale   : Main.TREE_SCALE,
                    image   : null,
                    offsetX : Main.TREE_OFFSET_X,
                    offsetY : Main.TREE_OFFSET_Y
            ],
            (Shape.STONE)      : [
                    fileName: 'icons8-silver-ore-48.png',
                    scale   : Main.STONE_SCALE,
                    image   : null,
                    offsetX : Main.STONE_OFFSET_X,
                    offsetY : Main.STONE_OFFSET_Y
            ],
            (Shape.WARRIOR)    : [
                    fileName: 'icons8-iron-age-warrior-48.png',
                    scale   : Main.VILLAGER_SCALE,
                    image   : null,
                    offsetX : Main.PERSON_OFFSET_X,
                    offsetY : Main.PERSON_OFFSET_Y
            ],
            (Shape.SHAMAN)     : [
                    fileName: 'icons8-spartan-helmet-48.png',
                    scale   : Main.VILLAGER_SCALE,
                    image   : null,
                    offsetX : Main.PERSON_OFFSET_X,
                    offsetY : Main.PERSON_OFFSET_Y
            ],
            (Shape.SHAMAN_CAMP): [
                    fileName: 'icons8-campfire-48.png',
                    scale   : Main.VILLAGER_SCALE,
                    image   : null,
                    offsetX : Main.PERSON_OFFSET_X,
                    offsetY : Main.PERSON_OFFSET_Y
            ],
            (Shape.SHAMAN_BUILD): [
                    fileName: 'flaticon-hammer-32.png',
                    scale   : Main.VILLAGER_SCALE,
                    image   : null,
                    offsetX : Main.PERSON_OFFSET_X,
                    offsetY : Main.PERSON_OFFSET_Y
            ],
            (Shape.FOLLOWER)   : [
                    fileName: 'icons8-sword-48.png',
                    scale   : Main.VILLAGER_SCALE,
                    image   : null,
                    offsetX : Main.PERSON_OFFSET_X,
                    offsetY : Main.PERSON_OFFSET_Y
            ]
    ]

    static def init(def keyboard, def mouse) {
        Model.keyboard = keyboard
        Model.mouse = mouse
        tileNetwork = Background.generateBackground()
        backgroundImage = createBGImage()
        shapeProperties[Shape.TREE].image = createImage(Shape.TREE)
        shapeProperties[Shape.STONE].image = createImage(Shape.STONE)
        shapeProperties[Shape.WARRIOR].image = createImage(Shape.WARRIOR)
        shapeProperties[Shape.SHAMAN].image = createImage(Shape.SHAMAN)
        shapeProperties[Shape.SHAMAN_CAMP].image = createImage(Shape.SHAMAN_CAMP)
        shapeProperties[Shape.SHAMAN_BUILD].image = createImage(Shape.SHAMAN_BUILD)
        shapeProperties[Shape.FOLLOWER].image = createImage(Shape.FOLLOWER)
        def villagers = (1..2000).collect { Villager.test() }

        def drawables = new ConcurrentLinkedQueue<Drawable>([
                villagers
        ].flatten() as List<Drawable>)

        Model.villagers = villagers
        Model.drawables = drawables

        Background.setResources(tileNetwork)
    }

    static BufferedImage createImage(Shape shape) {

        def fileName = shapeProperties[shape].fileName as String
        def scale = shapeProperties[shape].scale as Double

        ClassLoader classloader = Thread.currentThread().getContextClassLoader()
        def img = ImageIO.read(classloader.getResourceAsStream(fileName))
        def scaledImage = new BufferedImage (
                (scale * img.getWidth(null)) as int,
                (scale * img.getHeight(null)) as int,
                BufferedImage.TYPE_INT_ARGB
        )

        Graphics2D g2d = (Graphics2D) scaledImage.getGraphics()
        g2d.scale(scale, scale)
        g2d.drawImage(img, 0, 0, null)
        g2d.dispose()

        scaledImage
    }

    static BufferedImage applyColorFilter(BufferedImage origImage, Color color) {
        def image = copyImage(origImage)
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = image.getRGB(x,y)

                int alpha = (pixel>>24)&0xff
                int red = (pixel>>16)&0xff
                int green = (pixel>>8)&0xff
                int blue = pixel&0xff

                int newRed = (((color.red - red) / Main.SHADE_TRIBE) + red) as int
                int newGreen = (((color.green - green) / Main.SHADE_TRIBE) + green) as int
                int newBlue = (((color.blue - blue) / Main.SHADE_TRIBE) + blue) as int

                pixel = (alpha<<24) | (newRed<<16) | (newGreen<<8) | newBlue

                image.setRGB(x, y, pixel)
            }
        }

        return image
    }

    static BufferedImage copyImage(BufferedImage source) {
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType())
        Graphics2D g = b.createGraphics()
        g.drawImage(source, 0, 0, null)
        g.dispose()
        return b
    }

    static BufferedImage shadeImage(BufferedImage image, Color c) {

        def c2 = getDominantColor(image)
        def gray1 = ((c.getRed() + c.getGreen() + c.getBlue()) / 3) as int
        def gray2 = ((c2.getRed() + c2.getGreen() + c2.getBlue()) / 3) as int

        float scaleFactor = ((gray1 / gray2) * Main.SHADE_IMAGES) as float
        RescaleOp op = new RescaleOp(scaleFactor, 0, null)
        op.filter(image, null)
    }

    static Color getDominantColor(BufferedImage image) {
        int redBucket = 0
        int greenBucket = 0
        int blueBucket = 0

        int pixelCount = 0
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                def color = new Color(image.getRGB(x, y))
                if (color.getAlpha() == 255) {
                    redBucket += color.getRed()
                    greenBucket += color.getGreen()
                    blueBucket += color.getBlue()
                    pixelCount++
                }
            }
        }

        int r = redBucket / pixelCount
        int g = greenBucket / pixelCount
        int b = blueBucket / pixelCount

        new Color(r, g, b)
    }

    static Color brightness(Color c, double scale) {
        int r = Math.min(255, (int) (c.getRed() * scale))
        int g = Math.min(255, (int) (c.getGreen() * scale))
        int b = Math.min(255, (int) (c.getBlue() * scale))
        new Color(r,g,b)
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
                if (drawable.shape == Shape.RECT) {
                    g2d.fillRect(drawable.x as int, drawable.y as int, drawable.size, drawable.size)
                }
            }
        }

        BufferedImage dest = new BufferedImage(
                tileNetwork.length * Main.TILE_WIDTH,
                tileNetwork[0].length * Main.TILE_WIDTH,
                BufferedImage.TYPE_INT_RGB
        )
        new GaussianFilter(Main.GAUSSIAN_FILTER).filter(image, dest)
        /*
        float[] blurMatrix = [
                1/14f, 2/14f, 1/14f,
                2/14f, 2/14f, 2/14f,
                1/14f, 2/14f, 1/14f
        ].toArray()
        new ConvolveFilter(blurMatrix).filter(image, dest)
        */
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
                Main.MAP_WIDTH / 2 + generate((Main.MAP_WIDTH / 2.15) as int),
                Main.MAP_HEIGHT / 2 + generate((Main.MAP_HEIGHT / 2.15) as int)
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

    static int[] closeRandomTile(Villager villager, Integer minTileDist, Integer maxTileDist, int depth = 0) {

        if (depth > 20) {
            return pixelToTileIdx(villager.x, villager.y)
        }

        def tileNetwork = Model.tileNetwork as Tile[][]
        def (int tileX, int tileY) = villager.getTileXY()

        List<int[]> tiles = []

        getTilesWithinRadii(tileX, tileY, maxTileDist) { int x, int y ->
            def tile = tileNetwork[x][y]
            def furtherThanMin = minTileDist ? !withinCircle(tileX, tileY, x, y, minTileDist) : true

            if (furtherThanMin && villager.canTravel(tile.travelType)) {
                tiles << tile.tileXY
            }
        }

        if (tiles) {
            return tiles[getRandomIntegerBetween(0, tiles.size() - 1)]
        } else {
            return pixelToTileIdx(villager.x, villager.y)
        }
    }

    static int[] centroidTile(List<Villager> villagers, Villager me, Integer maxDist) {
        def cPixel = [0, 0] as Double[]

        for (Villager villager: villagers) {
            cPixel[0] += villager.x
            cPixel[1] += villager.y
        }

        cPixel[0] = cPixel[0] / villagers.size()
        cPixel[1] = cPixel[1] / villagers.size()

        def cTile = pixelToTileIdx(cPixel)

        def tile = Model.tileNetwork[cTile[0]][cTile[1]] as Tile

        if (me.canTravel(tile.travelType)) {
            return cTile
        } else {
            return closeRandomTile(me, null, maxDist)
        }
    }

    static boolean closeEnough(Double[] pointA, Double[] pointB) {
        Double xBig = pointA[0] + Main.STEP
        Double xSmall = pointA[0] - Main.STEP
        Double yBig = pointA[1] + Main.STEP
        Double ySmall = pointA[1] - Main.STEP
        return pointB[0] <= xBig && pointB[0] >= xSmall && pointB[1] <= yBig && pointB[1] >= ySmall
    }

    static boolean closeEnoughTile(int[] tileA, int[] tileB) {
        int xBig = tileA[0] + 1
        int xSmall = tileA[0] - 1
        int yBig = tileA[1] + 1
        int ySmall = tileA[1] - 1
        return tileB[0] <= xBig && tileB[0] >= xSmall && tileB[1] <= yBig && tileB[1] >= ySmall
    }

    static List<Double[]> randomPlacesInTileList(List<int[]> tiles) {
        tiles.collect { def tile ->
            randomPlaceInTile(tile)
        }
    }

    static Double[] randomPlaceInTile(int[] tile) {
        if (Model.tileNetwork[tile[0]][tile[1]].travelType == TravelType.WATER) {
            throw new PerIsBorkenException()
        }
        def pixelIdx = tileToPixelIdx(tile)
        pixelIdx[0] += (Main.TILE_WIDTH - 2) * Math.random() + 1
        pixelIdx[1] += (Main.TILE_WIDTH - 2) * Math.random() + 1
        return pixelIdx
    }

    static int tileRange(Drawable a, Drawable b) {
        pixelToTileIdx(pixelRange(a, b))
    }

    static Double pixelRange(Drawable a, Drawable b) {
        return a.id != b.id ? (Math.sqrt(Math.pow((a.x - b.x), 2) + Math.pow((a.y - b.y), 2))) : java.lang.Double.MAX_VALUE
    }

    static Double generate(int distance) {
        return distance - ThreadLocalRandom.current().nextInt(0, distance * 2 + 1)
    }

    static int pixelToTileIdx(Double pixel) {
        pixel / Main.TILE_WIDTH
    }

    static int[] pixelToTileIdx(List<Double> pixels) {
        pixels.collect { it / Main.TILE_WIDTH }
    }

    static int[] pixelToTileIdx(Double a, Double b) {
        [ a / Main.TILE_WIDTH, b / Main.TILE_WIDTH ] as int[]
    }

    static int[] pixelToTileIdx(Double[] pixels) {
        pixels.collect { it / Main.TILE_WIDTH }
    }

    static Double tileToPixelIdx(int tile) {
        tile * Main.TILE_WIDTH
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


    //https://stackoverflow.com/questions/40779343/java-loop-through-all-pixels-in-a-2d-circle-with-center-x-y-and-radius?noredirect=1&lq=1
    static void getTilesWithinRadii(int tX, int tY, int r, Closure function) {
        // iterate through all y-coordinates
        for (int y = tY - r; y <= tY + r; y++) {
            // iterate through all x-coordinates
            for (int x = tX - r; x <= tX + r; x++) {
                if (withinCircle(x, y, tX, tY, r)) {
                    if (withinTileNetwork(x, y)) {
                        //TestPrints.printRadii(x, y, me)
                        function(x, y)
                    }
                }
            }
        }
    }

    static boolean withinCircle(int x, int y, int tX, int tY, int r) {
        //(x - center_x)^2 + (y - center_y)^2 < radius^2
        Math.pow(x - tX, 2) + Math.pow(y - tY, 2) <= Math.pow(r, 2)
    }

    static boolean withinTileNetwork(int x, int y) {
        x >= 0 && x <= Model.tileNetwork.length - 1 && y >= 0 && y <= Model.tileNetwork[0].length - 1
    }

    static Integer getRandomIntegerBetween(Integer min, Integer max) {
        return (min + (Math.random() * ((max + 1) - min))).toInteger()
    }
}
