package main.input

import main.Model
import main.drawers.Surface
import main.model.Villager
import main.things.Drawable
import main.utility.ImageUtils

import java.awt.*
import java.awt.event.MouseEvent
import java.awt.event.MouseListener

class MyMouseListener implements MouseListener {
    @Override
    void mouseClicked(MouseEvent e) {

        //bilder och ytor ritas från övre vänstra hörnet

        Model.drawables.findAll { Drawable drawable ->
            if (Model.shapeImageMap[drawable.shape]) {
                def a = e.point.x >= drawable.x + Surface.xOffset - 30
                def b = e.point.x <= drawable.x + Surface.xOffset + 30
                def c = e.point.y >= drawable.y + Surface.yOffset - 30
                def d = e.point.y <= drawable.y + Surface.yOffset + 30
                return a && b && c && d
            } else {
                return false
            }
        }.each { Drawable drawable ->
            def c = Color.lightGray
            def gray = ((c.getRed() + c.getGreen() + c.getBlue()) / 3) as int
            drawable.image = ImageUtils.shadeImageToMatch(Model.shapeImageMap[drawable.shape][0], gray)
            if (drawable instanceof Villager) {
                drawable.debug = true
                println((drawable as Villager).toString())
            }
        }
    }

    @Override
    void mousePressed(MouseEvent e) {

    }

    @Override
    void mouseReleased(MouseEvent e) {

    }

    @Override
    void mouseEntered(MouseEvent e) {

    }

    @Override
    void mouseExited(MouseEvent e) {

    }
}
