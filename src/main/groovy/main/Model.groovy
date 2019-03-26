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
        def imageWidth = image.getWidth()
        def imageHeight = image.getHeight()
        int[][] heightMap = new int[imageWidth][imageHeight]
        def min = 128
        def max = 128
        for(int x = 0; x < image.getWidth(); x++) {
            for(int y = 0; y < image.getHeight(); y++) {
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

                    if (blue != 0 && blue < min) {
                        min = blue
                    } else if (blue != 255 && blue > max) {
                        max = blue
                    }
                } else {
                    throw new PerIsBorkenException()
                }
            }
        }

        for(int x = 0; x < heightMap.length; x++) {
            for(int y = 0; y < heightMap[x].length; y++) {
                if (heightMap[x][y] > max) {
                    heightMap[x][y] = max
                }
                else if (heightMap[x][y] < min) {
                    heightMap[x][y] = min
                }
            }
        }

        int globalAdjustment = round((Math.abs(min-128) - Math.abs(max-128)) / 2)
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

        for(int x = 0; x < heightMap.length; x++) {
            for(int y = 0; y < heightMap[x].length; y++) {
                heightMap[x][y] = round((((heightMap[x][y] + globalAdjustment - 128) * scale) + 128 ))

                if (heightMap[x][y] < min) {
                    min = heightMap[x][y]
                } else if (heightMap[x][y] > max) {
                    max = heightMap[x][y]
                }
            }
        }

        if (max > 255 || min < 0) {
            throw new PerIsBorkenException()
        }

        def xRatio = imageWidth / WINDOW_WIDTH
        def yRatio =  imageHeight / WINDOW_HEIGHT

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

                if (noPixels > 0 && xNodeIdx < nodeNetwork.length && yNodeIdx < nodeNetwork[xNodeIdx].length){
                    def avgAreaHeight = round(sumAreaHeight / noPixels)

                    if (nodeNetwork[xNodeIdx][yNodeIdx]) {
                        throw new PerIsBorkenException()
                    }

                    nodeNetwork[xNodeIdx][yNodeIdx] = new Node(
                            height: avgAreaHeight,
                            size: squareWidth,
                            x: xNodeIdx * squareWidth,
                            y: yNodeIdx * squareHeight,
                            color: new Color(avgAreaHeight, avgAreaHeight, avgAreaHeight)
                    )
                }
                yNodeIdx++
            }
            yNodeIdx = 0
            xNodeIdx++
        }

        def controlMap = [:]

        for(int x = 0; x < pixelReadControl.length; x++) {
            for(int y = 0; y < pixelReadControl[x].length; y++) {
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

        for(int x = 0; x < nodeNetwork.length; x++) {
            for(int y = 0; y < nodeNetwork[x].length; y++) {
                if (!nodeNetwork[x][y]) {
                    throw new PerIsBorkenException()
                }
            }
        }

        //colors here

        return nodeNetwork
    }

    static int round (BigDecimal number) {
        number.setScale(0, BigDecimal.ROUND_HALF_DOWN).intValue()
    }

    static int round (double number) {
        Math.round(number)
    }

    static double[] generateXY() {
        (double[])[
                WINDOW_WIDTH / 2 + generate(WINDOW_WIDTH / 3 as int),
                WINDOW_HEIGHT / 2 + generate(WINDOW_HEIGHT / 3 as int)
        ]
    }

    static double generate(int distance) {
        return distance - ThreadLocalRandom.current().nextInt(0, distance * 2 + 1)
    }

}
