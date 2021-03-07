package main.utility

import main.Main
import main.Model
import main.exception.PerIsBorkenException
import main.model.Tile
import main.things.naturalResource.NaturalResource

import javax.imageio.ImageIO
import java.awt.*
import java.awt.image.BufferedImage
import java.util.List
import java.util.concurrent.ThreadLocalRandom

class BackgroundUtils {

    static Tile[][] generateBackground() {
        generateBackground("lol${(Math.random() * 4 + 1) as int}.png")
    }

    static Tile[][] generateBackground(String imageName) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader()
        BufferedImage image = ImageIO.read(classloader.getResourceAsStream(imageName))

        int[][] heightMap = buildHeightMap(image)
        shaveOffExtremeMaxMin(heightMap)
        maximizeScale(heightMap)
        Tile[][] tileNetwork = buildTileNetwork(heightMap)
        setColors(tileNetwork)

        return tileNetwork
    }

    private static int[][] buildHeightMap(BufferedImage image) {
        int[][] heightMap = new int[image.getWidth()][image.getHeight()]

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                def color = new Color(image.getRGB(x, y))
                int blue = color.getBlue()
                int green = color.getGreen()
                int red = color.getRed()
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
                heightMap[x][y] = (((heightMap[x][y] + globalAdjustment - middle) * scale) + middle)
                maxMin << heightMap[x][y]
            }
        }

        max = maxMin.max()
        min = maxMin.min()

        if (max != 255 || min != 0) {
            throw new PerIsBorkenException()
        }
    }

    private static Tile[][] buildTileNetwork(int[][] heightMap) {

        int imageWidth = heightMap.length
        int imageHeight = heightMap[0].length

        Double xRatio = imageWidth / Main.MAP_WIDTH
        Double yRatio = imageHeight / Main.MAP_HEIGHT

        int tileWidth = Main.TILE_WIDTH
        int tileHeight = tileWidth

        int bgWidth = Main.MAP_WIDTH / tileWidth
        int bgHeight = Main.MAP_HEIGHT / tileHeight

        Double xStep = tileWidth * xRatio
        Double yStep = tileHeight * yRatio

        def tileNetwork = new Tile[bgWidth][bgHeight]

        int xTileIdx = 0
        int yTileIdx = 0

        def pixelReadControl = new int[heightMap.length][heightMap[0].length]

        int max = 128
        int min = 128

        for (Double x = 0; Utility.round(x) < heightMap.length; x += xStep) {
            for (Double y = 0; Utility.round(y) < heightMap[Utility.round(x)].length; y += yStep) {
                int sumAreaHeight = 0
                int noPixels = 0

                for (int xx = Utility.round(x); xx < Math.min(Utility.round(x + xStep), heightMap.length); xx++) {
                    for (int yy = Utility.round(y); yy < Math.min(Utility.round(y + yStep), heightMap[xx].length); yy++) {
                        pixelReadControl[xx][yy] += 1
                        sumAreaHeight += heightMap[xx][yy]
                        noPixels++
                    }
                }

                if (noPixels > 0 && xTileIdx < tileNetwork.length && yTileIdx < tileNetwork[xTileIdx].length) {
                    int avgAreaHeight = sumAreaHeight / noPixels

                    if (avgAreaHeight < min) {
                        min = avgAreaHeight
                    } else if (avgAreaHeight > max) {
                        max = avgAreaHeight
                    }

                    if (tileNetwork[xTileIdx][yTileIdx]) {
                        throw new PerIsBorkenException()
                    }

                    def tile = new Tile (
                            height: avgAreaHeight,
                            size: tileWidth,
                            x: xTileIdx * tileWidth,
                            y: yTileIdx * tileHeight
                    )

                    tileNetwork[xTileIdx][yTileIdx] = tile
                }
                yTileIdx++
            }
            yTileIdx = 0
            xTileIdx++
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

        for (int x = 0; x < tileNetwork.length; x++) {
            for (int y = 0; y < tileNetwork[x].length; y++) {
                if (!tileNetwork[x][y]) {
                    throw new PerIsBorkenException()
                }
            }
        }
        return tileNetwork
    }

    private static void setColors(Tile[][] tileNetwork) {

        List<Tile> allTiles = []
        for (int x = 0; x < tileNetwork.length; x++) {
            for (int y = 0; y < tileNetwork[x].length; y++) {
                allTiles << tileNetwork[x][y]
            }
        }
        allTiles.sort { it.height }

        Color blueLow = new Color(51, 153, 255)
        Color blueHigh = new Color(153, 204, 255)
        Color yellowLow = new Color(194, 178, 128)
        Color yellowHigh = new Color(114, 100, 55)
        Color greenLow = new Color(102, 204, 0)
        Color greenHigh = new Color(0, 102, 51)
        Color mountainEdgeGreen = new Color(0, 100, 0)
        Color mountainLower = new Color(75, 75, 75)
        Color mountainLow = new Color(90, 90, 90)
        Color mountainHigh = new Color(255, 255, 255)
        def colorRatios = [
                [from: 0.0,  to: 0.18,  colorFrom: blueLow,           colorTo: blueHigh,         travelType: Model.TravelType.WATER],
                [from: 0.18,  to: 0.35, colorFrom: yellowLow,         colorTo: yellowHigh,       travelType: Model.TravelType.BEACH],
                [from: 0.35, to: 0.85, colorFrom: greenLow,          colorTo: greenHigh,        travelType: Model.TravelType.FOREST],
                [from: 0.85, to: 0.93, colorFrom: mountainEdgeGreen, colorTo: mountainLower,    travelType: Model.TravelType.HILL],
                [from: 0.93, to: 1.0,  colorFrom: mountainLow,       colorTo: mountainHigh,     travelType: Model.TravelType.MOUNTAIN]
        ]

        def controlMap = [:]
        def controlAllColors = []
        def controlUniqueHeightValues = []

        for (def colorRatio : colorRatios) {
            int from = colorRatio.from * allTiles.size()
            int to = colorRatio.to * allTiles.size()
            List<Tile> tileGroup = allTiles[from..to - 1]

            //remove from the back
            if (from > 0) {
                for (int i = tileGroup.size() - 1; i >= 0; i--) {
                    if (tileGroup[i].height == allTiles[from - 1].height) {
                        tileGroup.remove(i)
                    }
                }
            }

            //add to the front
            for (int i = to; i < allTiles.size(); i++) {
                if (tileGroup.last().height == allTiles[i].height) {
                    tileGroup << allTiles[i]
                }
            }

            def uniqueHeightValues = tileGroup.groupBy { it.height }.collect { it.key }

            List<Color> colors = ImageUtils.gradient(colorRatio.colorFrom, colorRatio.colorTo, uniqueHeightValues.size())
            for (int i = 0; i < uniqueHeightValues.size(); i++) {
                tileGroup.grep { it.height == uniqueHeightValues[i] }.each {
                    it.color = colors[i]
                    it.travelType = colorRatio.travelType
                    controlMap[it.id] = controlMap[it.id] ? controlMap[it.id] + 1 : 1
                }
            }
            controlAllColors << colors
            controlUniqueHeightValues << uniqueHeightValues
        }

        //test: use all colors
        if (!(controlAllColors.flatten()*.getRGB().unique().sort() == allTiles.color*.getRGB().unique().sort())) {
            throw new PerIsBorkenException()
        }

        //test: no two heights of tiles uses the same color
        allTiles.groupBy { it.height }.each { int height, List<Tile> tiles ->
            if (tiles.groupBy { it.color }.size() != 1) {
                throw new PerIsBorkenException()
            }
        }

        if (controlMap.collect { it.key }.sort() != allTiles.id.sort()) {
            throw new PerIsBorkenException()
        }
        controlMap.each {
            if (it.value != 1) {
                throw new PerIsBorkenException()
            }
        }

        if (allTiles.color.grep().size() != allTiles.size()) {
            allTiles.grep { !it.color }.each {
                it.color = new Color(255, 0, 0)
                it.size = 20
            }
            throw new PerIsBorkenException()
        }
    }

    static void setNaturalResources(Tile[][] tileNetwork) {
        for (int y = 0; y < tileNetwork[0].length; y++) {
            for (int x = 0; x < tileNetwork.length; x++) {
                Tile tile = tileNetwork[x][y]
                int random = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE)

                def naturalResources = Model.travelTypeNaturalResources[tile.travelType]

                naturalResources.each { Class<? extends NaturalResource> clazz, Integer prevalence ->
                    if (random % prevalence == 0) {
                        tile.naturalResources << clazz.getDeclaredConstructor(Tile.class).newInstance(tile)
                    }
                }
            }
        }
    }
}
