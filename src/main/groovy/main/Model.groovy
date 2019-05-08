package main

import main.exception.PerIsBorkenException
import main.rule.Rule
import main.rule.Walk
import main.things.Drawable
import main.villager.Villager

import javax.imageio.ImageIO
import java.awt.*
import java.awt.image.BufferedImage
import java.util.List
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ThreadLocalRandom

class Model {

    static int idGenerator = 0

    static model = [:]

    static int getNewId() {
        idGenerator++
    }

    enum TravelType {
        WATER, FOREST, HILL, MOUNTAIN, PLAIN, ROAD, UP_HILL, DOWN_HILL, EVEN
    }

    static def travelModifier = [
            (TravelType.WATER)    : 0.7d,
            (TravelType.FOREST)   : 1.0d,
            (TravelType.HILL)     : 1.1d,
            (TravelType.MOUNTAIN) : 1.2d,
            (TravelType.PLAIN)    : 0.9d,
            (TravelType.ROAD)     : 0.8d,

            (TravelType.UP_HILL)  : 1.1d,
            (TravelType.EVEN)     : 1.0d,
            (TravelType.DOWN_HILL): 1.0d,
    ]

    static def allSquares = [
            [-1, 1],  [0,  1],  [1,  1],
            [-1, 0],            [1,  0],
            [-1, -1], [0, -1],  [1, -1]
    ]

    static def squareDegrees = [
            [113, 158]: [-1,  1], [68, 113] : [0,  1], [23,   68]: [1,  1],
            [158, 203]: [-1,  0],                      [338,  23]: [1,  0],
            [203, 248]: [-1, -1], [248, 293]: [0, -1], [293, 338]: [1, -1]
    ]

    static def init(def keyboard, def mouse) {
        def nodeNetwork = generateBackground()

        model = [
                keyboard      : keyboard,
                mouse         : mouse,
                pause         : false,
                drawables     : [],
                villagers     : [],
                frameSlots    : [0, 0, 0, 0, 0],
                nodeNetwork   : nodeNetwork,
                rules         : generateRules()
        ]

        model.squareProbabilitiesForDegrees = calculateProbabilitiesModel()

        def villagers = [
                new Villager(), new Villager(), new Villager(), new Villager(), new Villager()
        ]
        def stones = []
        def trees = []
        def artifacts = []

        def drawables = new ConcurrentLinkedQueue<Drawable>([
                artifacts, stones, trees, villagers
        ].flatten() as List<Drawable>)

        model.villagers = villagers
        model.drawables = drawables

        model.backgroundImage = createBGImage()
    }

    static List<Rule> generateRules() {
        int rank = Integer.MAX_VALUE
        [new Walk(rank: --rank)]
    }

    static def calculateProbabilitiesModel() {
        if (
                degreeRange(45) != (315..359) + (0..135) ||
                degreeRange(100) != 10..190 ||
                degreeRange(300) != (210..359) + (0..30)
        ) {
            throw new PerIsBorkenException()
        }

        (0..359).collectEntries { def degree ->
            def degreeRange = degreeRange(degree)
            def degreeProbabilities = degreeProbabilities(degreeRange)
            def squares = squareProbabilities(degreeProbabilities)

            def random = round(Math.random() * 10)
            if (random % 10 == 0) {
                if (degreeProbabilities.collect { it[0] } != degreeRange) {
                    throw new PerIsBorkenException()
                }

                if (Math.abs((degreeProbabilities.sum { it[1] } as Double) - 100) > 0.00000001) {
                    throw new PerIsBorkenException()
                }

                if (Math.abs((squares.collect { it[1] }.sum() as Double) - 100) > 0.00000001) {
                    throw new PerIsBorkenException()
                }

                reverseEngineerDegree(degree, squares)
            }

            [(degree), squares]
        }
    }

    static void reverseEngineerDegree(int realDegree, def squares) {

        def vectors = squares.collect { def square ->
            [square[1] * square[0][0], square[1] * square[0][1]]
        }

        def addedVector = vectors.inject([0, 0]) { def result, def elem ->
            result[0] += elem[0]
            result[1] += elem[1]
            return result
        }

        def l = Math.toDegrees(Math.atan2(addedVector[1], addedVector[0]))

        def reversed = l >= 0 ? l : l + 360

        //https://gamedev.stackexchange.com/questions/4467/comparing-angles-and-working-out-the-difference
        def diffDeg = 180.0 - Math.abs(Math.abs(reversed - realDegree) - 180.0)

        if (diffDeg > 1.6) {
            throw new PerIsBorkenException()
        }
    }

    private static List<Integer> degreeRange(int degree) {
        int u = degree + 90
        int l = degree - 90
        int upper = u % 360
        int lower = l >= 0 ? l : l + 360
        (upper > lower) ? (lower..upper) : (lower..359) + (0..upper)
    }

    private static List<List<Number>> degreeProbabilities(List<Integer> degree) {
        (degree[0..35]).collect    { [it, 12.5/36] } +
        (degree[36..71]).collect   { [it, 25/36] } +
        (degree[72..108]).collect  { [it, 25/37] } +
        (degree[109..144]).collect { [it, 25/36] } +
        (degree[145..180]).collect { [it, 12.5/36] }
    }

    private static List<List<Object>> squareProbabilities(List<List<Number>> degreeProbabilities) {
        Model.squareDegrees.collect { def square ->
            def squareDegrees = square.key
            def squareProbability = degreeProbabilities.sum { def degreeProbability ->
                def degree = degreeProbability[0] as int
                def probability = degreeProbability[1] as Double
                if (squareDegrees[0] < squareDegrees[1]) {
                    (degree >= squareDegrees[0] && degree < squareDegrees[1]) ? probability : 0
                } else {
                    ((degree >= squareDegrees[0] && degree < 360) || (degree < squareDegrees[1])) ? probability : 0
                }

            }
            [square.value, squareProbability]
        }
    }

    static Node[][] generateBackground() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader()
        BufferedImage image = ImageIO.read(classloader.getResourceAsStream('lol.png'))

        int[][] heightMap = buildHeightMap(image)
        shaveOffExtremeMaxMin(heightMap)
        maximizeScale(heightMap)
        Node[][] nodeNetwork = buildNodeNetwork(heightMap)
        setColors(nodeNetwork)

        return nodeNetwork
    }

    static BufferedImage createBGImage() {
        Node[][] nodeNetwork = model.nodeNetwork
        BufferedImage image = new BufferedImage(
                nodeNetwork.length * Main.SQUARE_WIDTH,
                nodeNetwork[0].length * Main.SQUARE_WIDTH,
                BufferedImage.TYPE_INT_RGB
        )
        Graphics2D g2d = image.createGraphics()

        for (int x = 0; x < nodeNetwork.length; x++) {
            for (int y = 0; y < nodeNetwork[x].length; y++) {
                Drawable drawable = nodeNetwork[x][y]
                g2d.setPaint(drawable.color)
                if (drawable.shape == Drawable.SHAPES.RECT) {
                    g2d.fillRect(round(drawable.x), round(drawable.y), drawable.size, drawable.size)
                }
            }
        }

        return image
    }

    private static int[][] buildHeightMap(BufferedImage image) {
        int[][] heightMap = new int[image.getWidth()][image.getHeight()]

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                /*
                    (getAlpha(inData) << 24)
                    | (getRed(inData) << 16)
                    | (getGreen(inData) << 8)
                    | (getBlue(inData) << 0)
                 */
                int rgb = image.getRGB(x, y)
                int blue = rgb & 0x0000FF
                int green = rgb & 0x00FF00 >> 8
                int red = rgb & 0xFF0000 >> 16
                if (blue == green && blue == red) {
                    heightMap[x][y] = blue
                } else {
                    throw new PerIsBorkenException()
                }
            }
        }
        return heightMap
    }

    private static void shaveOffExtremeMaxMin(int[][] heightMap) {

        List<Integer> maxMin = []
        for (int x = 0; x < heightMap.length; x++) {
            for (int y = 0; y < heightMap[x].length; y++) {
                maxMin << heightMap[x][y]
            }
        }
        int max = maxMin.max()
        int min = maxMin.min()

        int nextMax = maxMin.toSet().sort().reverse()[1]
        int nextMin = maxMin.toSet().sort()[1]
        boolean shaveMax = Math.abs(max - nextMax) > 5
        boolean shaveMin = Math.abs(min - nextMin) > 5

        for (int x = 0; x < heightMap.length; x++) {
            for (int y = 0; y < heightMap[x].length; y++) {
                if (shaveMax && heightMap[x][y] == max) {
                    heightMap[x][y] = nextMax
                }
                if (shaveMin && heightMap[x][y] == min) {
                    heightMap[x][y] = nextMin
                }
            }
        }
    }

    private static void maximizeScale(int[][] heightMap) {
        Double middle = 255 / 2
        List<Integer> maxMin = []
        for (int x = 0; x < heightMap.length; x++) {
            for (int y = 0; y < heightMap[x].length; y++) {
                maxMin << heightMap[x][y]
            }
        }

        int max = maxMin.max()
        int min = maxMin.min()

        Double globalAdjustment = middle - ((max + min) / 2)
        Double newMax = max + globalAdjustment - middle
        Double newMin = min + globalAdjustment - middle
        Double scaleMax = middle / newMax
        Double scaleMin = -middle / newMin

        if (scaleMax != scaleMin) {
            throw new PerIsBorkenException()
        }

        Double scale = scaleMax

        maxMin.clear()
        for (int x = 0; x < heightMap.length; x++) {
            for (int y = 0; y < heightMap[x].length; y++) {
                heightMap[x][y] = round((((heightMap[x][y] + globalAdjustment - middle) * scale) + middle))
                maxMin << heightMap[x][y]
            }
        }

        max = maxMin.max()
        min = maxMin.min()

        if (max != 255 || min != 0) {
            throw new PerIsBorkenException()
        }
    }

    private static Node[][] buildNodeNetwork(int[][] heightMap) {

        int imageWidth = heightMap.length
        int imageHeight = heightMap[0].length

        Double xRatio = imageWidth / Main.MAP_WIDTH
        Double yRatio = imageHeight / Main.MAP_HEIGHT

        int squareWidth = Main.SQUARE_WIDTH
        int squareHeight = squareWidth

        int bgWidth = round(Main.MAP_WIDTH / squareWidth)
        int bgHeight = round(Main.MAP_HEIGHT / squareHeight)

        Double xStep = squareWidth * xRatio
        Double yStep = squareHeight * yRatio

        def nodeNetwork = new Node[bgWidth][bgHeight]

        int xNodeIdx = 0
        int yNodeIdx = 0

        def pixelReadControl = new int[heightMap.length][heightMap[0].length]

        int max = 128
        int min = 128

        for (Double x = 0; round(x) < heightMap.length; x += xStep) {
            for (Double y = 0; round(y) < heightMap[round(x)].length; y += yStep) {
                int sumAreaHeight = 0
                int noPixels = 0

                for (int xx = round(x); xx < Math.min(round(x + xStep), heightMap.length); xx++) {
                    for (int yy = round(y); yy < Math.min(round(y + yStep), heightMap[xx].length); yy++) {
                        pixelReadControl[xx][yy] += 1
                        sumAreaHeight += heightMap[xx][yy]
                        noPixels++
                    }
                }

                if (noPixels > 0 && xNodeIdx < nodeNetwork.length && yNodeIdx < nodeNetwork[xNodeIdx].length) {
                    int avgAreaHeight = round(sumAreaHeight / noPixels)

                    if (avgAreaHeight < min) {
                        min = avgAreaHeight
                    } else if (avgAreaHeight > max) {
                        max = avgAreaHeight
                    }

                    if (nodeNetwork[xNodeIdx][yNodeIdx]) {
                        throw new PerIsBorkenException()
                    }

                    nodeNetwork[xNodeIdx][yNodeIdx] = new Node(
                            height: avgAreaHeight,
                            size: squareWidth,
                            x: xNodeIdx * squareWidth,
                            y: yNodeIdx * squareHeight
                    )
                }
                yNodeIdx++
            }
            yNodeIdx = 0
            xNodeIdx++
        }

        def controlMap = [:]

        for (int x = 0; x < pixelReadControl.length; x++) {
            for (int y = 0; y < pixelReadControl[x].length; y++) {
                def xy = controlMap["${pixelReadControl[x][y]}"]
                if (xy == null) {
                    controlMap["${pixelReadControl[x][y]}"] = 1
                } else {
                    controlMap["${pixelReadControl[x][y]}"] += 1
                }
            }
        }

        if (controlMap['1'] != imageHeight * imageWidth) {
            throw new PerIsBorkenException()
        }

        for (int x = 0; x < nodeNetwork.length; x++) {
            for (int y = 0; y < nodeNetwork[x].length; y++) {
                if (!nodeNetwork[x][y]) {
                    throw new PerIsBorkenException()
                }
            }
        }
        return nodeNetwork
    }

    private static void setColors(Node[][] nodeNetwork) {

        List<Node> allNodes = []
        for (int x = 0; x < nodeNetwork.length; x++) {
            for (int y = 0; y < nodeNetwork[x].length; y++) {
                allNodes << nodeNetwork[x][y]
            }
        }
        allNodes.sort { it.height }

        Color blueLow = new Color(51, 153, 255)
        Color blueHigh = new Color(153, 204, 255)
        Color greenLow = new Color(102, 204, 0)
        Color greenHigh = new Color(0, 102, 51)
        Color mountainEdgeGreen = new Color(0, 100, 0)
        Color mountainLower = new Color(75, 75, 75)
        Color mountainLow = new Color(90, 90, 90)
        Color mountainHigh = new Color(255, 255, 255)
        def colorRatios = [
                [from: 0.0,  to: 0.2,  colorFrom: blueLow,           colorTo: blueHigh,         travelType: TravelType.WATER],
                [from: 0.2,  to: 0.85, colorFrom: greenLow,          colorTo: greenHigh,        travelType: TravelType.FOREST],
                [from: 0.85, to: 0.93, colorFrom: mountainEdgeGreen, colorTo: mountainLower,    travelType: TravelType.HILL],
                [from: 0.93, to: 1.0,  colorFrom: mountainLow,       colorTo: mountainHigh,     travelType: TravelType.MOUNTAIN]
        ]

        def controlMap = [:]
        def controlAllColors = []
        def controlUniqueHeightValues = []

        for (def colorRatio : colorRatios) {
            int from = round(colorRatio.from * allNodes.size())
            int to = round(colorRatio.to * allNodes.size())
            List<Node> nodeGroup = allNodes[from..to - 1]

            //remove from the back
            if (from > 0) {
                for (int i = nodeGroup.size() - 1; i >= 0; i--) {
                    if (nodeGroup[i].height == allNodes[from - 1].height) {
                        nodeGroup.remove(i)
                    }
                }
            }

            //add to the front
            for (int i = to; i < allNodes.size(); i++) {
                if (nodeGroup.last().height == allNodes[i].height) {
                    nodeGroup << allNodes[i]
                }
            }

            def uniqueHeightValues = nodeGroup.groupBy { it.height }.collect { it.key }

            List<Color> colors = gradient(colorRatio.colorFrom, colorRatio.colorTo, uniqueHeightValues.size())
            for (int i = 0; i < uniqueHeightValues.size(); i++) {
                nodeGroup.grep { it.height == uniqueHeightValues[i] }.each {
                    it.color = colors[i]
                    it.travelType = colorRatio.travelType
                    controlMap[it.id] = controlMap[it.id] ? controlMap[it.id] + 1 : 1
                }
            }
            controlAllColors << colors
            controlUniqueHeightValues << uniqueHeightValues
        }

        //test: use all colors
        if (!(controlAllColors.flatten()*.getRGB().unique().sort() == allNodes.color*.getRGB().unique().sort())) {
            throw new PerIsBorkenException()
        }

        //test: no two heights of nodes uses the same color
        allNodes.groupBy { it.height }.each { int height, List<Node> nodes ->
            if (nodes.groupBy { it.color }.size() != 1) {
                throw new PerIsBorkenException()
            }
        }

        if (controlMap.collect { it.key }.sort() != allNodes.id.sort()) {
            throw new PerIsBorkenException()
        }
        controlMap.each {
            if (it.value != 1) {
                throw new PerIsBorkenException()
            }
        }

        if (allNodes.color.grep().size() != allNodes.size()) {
            allNodes.grep { !it.color }.each {
                it.color = new Color(255, 0, 0)
                it.size = 20
            }
            throw new PerIsBorkenException()
        }
    }

    static List<Color> gradient(Color color1, Color color2, int steps) {
        def colors = []

        for (int i = 0; i < steps; i++) {
            Double ratio = i / steps
            int r = round(color2.getRed() * ratio + color1.getRed() * (1 - ratio))
            int g = round(color2.getGreen() * ratio + color1.getGreen() * (1 - ratio))
            int b = round(color2.getBlue() * ratio + color1.getBlue() * (1 - ratio))
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
        def xy = [
                Main.MAP_WIDTH / 2 + generate(round(Main.MAP_WIDTH / 9)),
                Main.MAP_HEIGHT / 2 + generate(round(Main.MAP_HEIGHT / 9))
        ]

        def nodeXY = pixelToNodeIdx(round(xy))

        def node = Model.model.nodeNetwork[nodeXY[0]][nodeXY[1]] as Node

        if (node.travelType == TravelType.WATER) {
            return generateXY()
        } else {
            return xy
        }
    }

    static Double generate(int distance) {
        return distance - ThreadLocalRandom.current().nextInt(0, distance * 2 + 1)
    }

    static int[] pixelToNodeIdx(int[] pixels) {
        pixels.collect { it / Main.SQUARE_WIDTH }
    }

    static int[] pixelToNodeIdx(Double[] pixels) {
        pixelToNodeIdx(round(pixels))
    }

    static int calculateDegree(int[] start, int[] dest) {
        Double deg = Math.toDegrees(Math.atan2(dest[1] - start[1], dest[0] - start[0]))
        Model.round(deg >= 0 ? deg : deg + 360)
    }

    static int calculateDegree(Double[] start, Double[] dest) {
        calculateDegree(round(start), round(dest))
    }

    static List<int[]> bresenham(int[] start, int[] dest) {
        def (int x1, int y1) = start
        def (int x2, int y2) = dest
        def result = []

        // delta of exact value and rounded value of the dependent variable
        int d = 0

        int dx = Math.abs(x2 - x1)
        int dy = Math.abs(y2 - y1)

        int dx2 = 2 * dx // slope scaling factors to
        int dy2 = 2 * dy // avoid floating point

        int ix = x1 < x2 ? 1 : -1 // increment direction
        int iy = y1 < y2 ? 1 : -1

        int x = x1
        int y = y1

        if (dx >= dy) {
            while (true) {
                result << ([x, y] as int[])
                if (x == x2) {
                    break
                }
                x += ix
                d += dy2
                if (d > dx) {
                    y += iy
                    d -= dx2
                }
            }
        } else {
            while (true) {
                result << ([x, y] as int[])
                if (y == y2) {
                    break
                }
                y += iy
                d += dx2
                if (d > dy) {
                    x += ix
                    d -= dy2
                }
            }
        }

        return result
    }
}
