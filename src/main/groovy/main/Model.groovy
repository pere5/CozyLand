package main

import main.exception.PerIsBorkenException
import main.person.Person
import main.things.Drawable
import main.things.Stone
import main.things.Tree

import javax.imageio.ImageIO
import java.awt.Color
import java.awt.image.BufferedImage
import java.math.RoundingMode
import java.util.concurrent.ThreadLocalRandom

class Model {

    static int WINDOW_WIDTH = 1000
    static int WINDOW_HEIGHT = 750

    static int idGenerator = 0

    static model

    static int getNewId() {
        idGenerator++
    }

    static def init() {
        def persons = [
                new Person(), new Person(), new Person(), new Person(), new Person()
        ]
        def stones = [
                new Stone(), new Stone(), new Stone(), new Stone(), new Stone(),
                new Stone(), new Stone(), new Stone(), new Stone(), new Stone(),
                new Stone(), new Stone(), new Stone(), new Stone(), new Stone(),
                new Stone(), new Stone(), new Stone(), new Stone(), new Stone(),
                new Stone(), new Stone(), new Stone(), new Stone(), new Stone(),
                new Stone(), new Stone(), new Stone(), new Stone(), new Stone()
        ]
        def trees = [
                new Tree(), new Tree(), new Tree(), new Tree(), new Tree(),
                new Tree(), new Tree(), new Tree(), new Tree(), new Tree(),
                new Tree(), new Tree(), new Tree(), new Tree(), new Tree(),
                new Tree(), new Tree(), new Tree(), new Tree(), new Tree(),
                new Tree(), new Tree(), new Tree(), new Tree(), new Tree(),
                new Tree(), new Tree(), new Tree(), new Tree(), new Tree()
        ]

        def drawables = [
                stones, trees, persons
        ].flatten()

        def nodeNetwork = generateBackground()

        def model = [
                pause: false,
                drawables: drawables,
                persons: persons,
                stones: stones,
                trees: trees,
                nodeNetwork: nodeNetwork
        ]
        this.model = model
    }

    static Node[][] generateBackground() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader()
        BufferedImage image = ImageIO.read(classloader.getResourceAsStream('lol.png'))

        int[][] heightMap = maximizeScale(image)

        Node[][] nodeNetwork = buildNodeNetwork(heightMap)

        setColors(nodeNetwork)

        return nodeNetwork
    }

    private static int[][] maximizeScale(BufferedImage image) {
        int[][] heightMap = new int[image.getWidth()][image.getHeight()]

        def min = 128
        def max = 128

        def shave = 10

        def shaveOffMax = 255 - shave
        def shaveOffMin = 0 + shave

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                /*
                    (getAlpha(inData) << 24)
                    | (getRed(inData) << 16)
                    | (getGreen(inData) << 8)
                    | (getBlue(inData) << 0)
                 */
                int rgb = image.getRGB(x, y)
                def blue = rgb & 0x0000FF
                def green = rgb & 0x00FF00 >> 8
                def red = rgb & 0xFF0000 >> 16
                if (blue == green && blue == red) {
                    heightMap[x][y] = blue

                    if (blue > shaveOffMin && blue < min) {
                        min = blue
                    } else if (blue < shaveOffMax && blue > max) {
                        max = blue
                    }
                } else {
                    throw new PerIsBorkenException()
                }
            }
        }

        for (int x = 0; x < heightMap.length; x++) {
            for (int y = 0; y < heightMap[x].length; y++) {
                if (heightMap[x][y] > max) {
                    heightMap[x][y] = max
                } else if (heightMap[x][y] < min) {
                    heightMap[x][y] = min
                }
            }
        }

        int globalAdjustment = round((Math.abs(min - 128) - Math.abs(max - 128)) / 2)
        max = max + globalAdjustment - 128
        min = min + globalAdjustment - 128
        def scaleMax = 128 / max
        def scaleMin = -128 / min

        if (!(scaleMax + 1 >= scaleMin && scaleMax - 1 <= scaleMin)) {
            throw new PerIsBorkenException()
        }

        def scale = Math.min(scaleMax, scaleMin)
        max = 128
        min = 128

        for (int x = 0; x < heightMap.length; x++) {
            for (int y = 0; y < heightMap[x].length; y++) {
                heightMap[x][y] = round((((heightMap[x][y] + globalAdjustment - 128) * scale) + 128))

                if (heightMap[x][y] < min) {
                    min = heightMap[x][y]
                } else if (heightMap[x][y] > max) {
                    max = heightMap[x][y]
                }
            }
        }

        if (max > 255 || max < 253 || min > 1 || min < 0) {
            throw new PerIsBorkenException()
        }
        return heightMap
    }

    private static Node[][] buildNodeNetwork(int[][] heightMap) {

        def imageWidth = heightMap.length
        def imageHeight = heightMap[0].length

        def xRatio = imageWidth / WINDOW_WIDTH
        def yRatio = imageHeight / WINDOW_HEIGHT

        def squareWidth = 6
        def squareHeight = squareWidth

        def bgWidth = round(WINDOW_WIDTH / squareWidth)
        def bgHeight = round(WINDOW_HEIGHT / squareHeight)

        def xStep = squareWidth * xRatio
        def yStep = squareHeight * yRatio

        def nodeNetwork = new Node[bgWidth][bgHeight]

        def xNodeIdx = 0
        def yNodeIdx = 0

        def pixelReadControl = new int[heightMap.length][heightMap[0].length]

        def max = 128
        def min = 128

        for (def x = 0.0; x < heightMap.length; x += xStep) {
            for (def y = 0.0; y < heightMap[round(x)].length; y += yStep) {
                def sumAreaHeight = 0
                def noPixels = 0

                for (int xx = round(x); xx < Math.min(round(x + xStep), heightMap.length); xx++) {
                    for (int yy = round(y); yy < Math.min(round(y + yStep), heightMap[xx].length); yy++) {
                        pixelReadControl[xx][yy] += 1
                        sumAreaHeight += heightMap[xx][yy]
                        noPixels++
                    }
                }

                if (noPixels > 0 && xNodeIdx < nodeNetwork.length && yNodeIdx < nodeNetwork[xNodeIdx].length) {
                    def avgAreaHeight = round(sumAreaHeight / noPixels)

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

        if (controlMap['1'] as int != imageHeight * imageWidth) {
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
        Color blueLow = new Color(51, 153, 255)
        Color blueHigh = new Color(153, 204, 255)
        Color greenLow = new Color(102, 204, 0)
        Color greenHigh = new Color(0, 102, 51)
        Color mountainLow = new Color(170, 170, 170)
        Color mountainHigh = new Color(250, 250, 250)
        def colorRatios = [
                [
                        from  : 0.0, to: 0.2, subNodes: null,
                        colors: gradient(blueLow, blueHigh, 7),
                ],
                [
                        from  : 0.2, to: 0.85, subNodes: null,
                        colors: gradient(greenLow, greenHigh, 7),
                ],
                [
                        from  : 0.85, to: 0.92, subNodes: null,
                        colors: gradient(greenHigh, mountainLow, 7),
                ],
                [
                        from  : 0.92, to: 1.0, subNodes: null,
                        colors: gradient(mountainLow, mountainHigh, 7),
                ]
        ]

        List<Node> allNodes = []
        for (int x = 0; x < nodeNetwork.length; x++) {
            for (int y = 0; y < nodeNetwork[x].length; y++) {
                allNodes << nodeNetwork[x][y]
            }
        }
        allNodes.sort { it.height }

        //transfer nodes of the same height as the last of the previous level down one level
        colorRatios.each { def colorRatio ->
            int from = colorRatio.from * allNodes.size()
            int to = colorRatio.to * allNodes.size()
            def subNodes = allNodes[from..to - 1]

            //remove from the back
            if (from > 0) {
                for (int i = subNodes.size() - 1; i >= 0; i--) {
                    if (subNodes[i].height == allNodes[from - 1].height) {
                        subNodes.remove(i)
                    }
                }
            }

            //add to the front
            for (int i = to; i < allNodes.size(); i++) {
                if (subNodes.last().height == allNodes[i].height) {
                    subNodes << allNodes[i]
                }
            }

            colorRatio.subNodes = subNodes
        }

        //Check that all nodes are present as subNodes
        def allSubNodes = colorRatios.subNodes.flatten() as List<Node>
        if (!(allNodes.size() == allSubNodes.size() && allSubNodes.size() == allSubNodes.id.toSet().size())) {
            throw new PerIsBorkenException()
        }

        def nodeControl = [] as List<Node>

        colorRatios.each { def colorRatio ->
            def colors = colorRatio.colors as List<Color>
            def subNodes = colorRatio.subNodes as List<Node>

            def mapColorToHeight = true
            def spreadColorLinearly = !mapColorToHeight

            if (spreadColorLinearly) {
                def step = subNodes.size() / colors.size()
                def colorIdx = 0
                for (def i = 0.0; i < subNodes.size(); i += step) {
                    def color = colors[colorIdx]
                    for (int j = round(i); j < Math.min(round(i + step), subNodes.size()); j++) {
                        subNodes[j].color = color
                        nodeControl << subNodes[j]
                    }
                    colorIdx++
                }
            } else if (mapColorToHeight) {
                def maxHeight = subNodes.height.max() as int
                def minHeight = subNodes.height.min() as int

                def colorStep = (maxHeight - minHeight) / colors.size()

                int i = 0
                for (BigDecimal height = minHeight; height <= maxHeight; height += colorStep) {
                    subNodes.each { Node node ->
                        if (node.height >= height && node.height < height + colorStep) {
                            node.color = colors[i]
                            nodeControl << node
                        }
                    }
                    i++
                }
            }
        }

        if (!(allNodes.size() == nodeControl.size() && nodeControl.size() == nodeControl.id.toSet().size())) {
            throw new PerIsBorkenException()
        }
    }

    private static List<Color> gradient(Color color1, Color color2, int steps) {
        def colors = []

        for (int i = 0; i < steps; i++) {
            def ratio = i / steps
            int r = (color2.getRed() * ratio + color1.getRed() * (1 - ratio)) as int
            int g = (color2.getGreen() * ratio + color1.getGreen() * (1 - ratio)) as int
            int b = (color2.getBlue() * ratio + color1.getBlue() * (1 - ratio)) as int
            colors << new Color(r, g, b)
        }
        colors
    }

    static int round (BigDecimal number) {
        number.setScale(0, BigDecimal.ROUND_HALF_DOWN).intValue()
    }

    static int round (double number) {
        Math.round(number)
    }

    static double[] generateXY() {
        [
                WINDOW_WIDTH / 2 + generate(WINDOW_WIDTH / 3 as int),
                WINDOW_HEIGHT / 2 + generate(WINDOW_HEIGHT / 3 as int)
        ]
    }

    static double generate(int distance) {
        return distance - ThreadLocalRandom.current().nextInt(0, distance * 2 + 1)
    }

}
