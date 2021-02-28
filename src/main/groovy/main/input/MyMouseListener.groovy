package main.input


import main.Model
import main.calculator.Utility
import main.things.Drawable

import java.awt.*
import java.awt.event.MouseEvent
import java.awt.event.MouseListener

class MyMouseListener implements MouseListener {
    @Override
    void mouseClicked(MouseEvent e) {

        //bilder och ytor ritas från övre vänstra hörnet

        Model.drawables.findAll { Drawable drawable ->
            Model.shapeProperties[drawable.shape].image && e.point.x >= drawable.x - 30 && e.point.x <= drawable.x + 30 && e.point.y >= drawable.y - 30 && e.point.y <= drawable.y + 30
        }.each { Drawable drawable ->
            drawable.image = Utility.shadeImage(Model.shapeProperties[drawable.shape].image, Color.lightGray)
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
