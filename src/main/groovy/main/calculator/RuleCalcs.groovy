package main.calculator

import main.Model
import main.things.Drawable

class RuleCalcs {

    static int tileRange(Drawable a, Drawable b) {
        Model.pixelToTileIdx(pixelRange(a, b))
    }

    static Double pixelRange(Drawable a, Drawable b) {
        return a.id != b.id ? (Math.sqrt(Math.pow((a.x - b.x), 2) + Math.pow((a.y - b.y), 2))) : Double.MAX_VALUE
    }

    static Double[] centroid(List<Drawable> drawables) {
        def centroid = [0, 0]

        for (Drawable drawable: drawables) {
            centroid[0] += drawable.x
            centroid[1] += drawable.y
        }

        int totalPoints = drawables.size()
        centroid[0] = centroid[0] / totalPoints
        centroid[1] = centroid[1] / totalPoints

        return centroid
    }
}
