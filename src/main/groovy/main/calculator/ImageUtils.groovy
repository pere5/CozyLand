package main.calculator

import javaSrc.color.GaussianFilter
import main.Main
import main.Model
import main.model.Tile
import main.things.Drawable

import javax.imageio.ImageIO
import java.awt.*
import java.awt.image.BufferedImage
import java.awt.image.RescaleOp
import java.util.List

class ImageUtils {

    static BufferedImage createImage(Drawable.Shape shape) {

        def fileName = Model.shapeProperties[shape].fileName as String
        def scale = Model.shapeProperties[shape].scale as Double

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

    static BufferedImage createBGImage(Tile[][] tileNetwork) {
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
                if (drawable.shape == Drawable.Shape.RECT) {
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
}
