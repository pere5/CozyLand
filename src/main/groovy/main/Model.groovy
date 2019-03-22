package main

import main.exception.PerIsBorkenException
import main.person.Person
import main.things.Stone
import main.things.Tree

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.math.RoundingMode

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
                persons, stones, trees
        ].flatten()

        def background = generateBackground()

        def model = [
                pause: false,
                drawables: drawables,
                background: background
        ]
        this.model = model
    }

    static def generateBackground() {
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

        int globalAdjustment = ((Math.abs(min-128) - Math.abs(max-128)) / 2).intValue()
        max = max + globalAdjustment - 128
        min = min + globalAdjustment - 128
        double scaleMax = 128 / max
        double scaleMin = -128 / min

        if (!(scaleMax + 1 >= scaleMin && scaleMax - 1 <= scaleMin)) {
            throw new PerIsBorkenException()
        }

        double scale = scaleMax

        for(int x = 0; x < heightMap.length; x++) {
            for(int y = 0; y < heightMap[x].length; y++) {
                heightMap[x][y] = (((heightMap[x][y] + globalAdjustment - 128) * scale) + 128 ).intValue()
            }
        }

        def bfWidth = WINDOW_WIDTH / 6
        def bgHeight = WINDOW_HEIGHT / 6

        def stepsX = (imageWidth / bfWidth) as int
        def stepsY = (imageHeight / bgHeight) as int

        for (int i = 0; i < imageWidth; i = i + stepsX) {
            for (int j = 0; j < imageHeight; j = j + stepsY) {

                for (int x = i; x < i + stepsX; x++) {
                    for (int y = j; y < j + stepsY; y++) {
                        if (x < imageWidth && y < imageHeight) {
                            //do stuff...
                        }
                    }
                }

            }
        }
    }
}
