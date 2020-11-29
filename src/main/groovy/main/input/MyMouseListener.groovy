package main.input

import groovyjarjarpicocli.CommandLine
import main.Model
import main.things.Drawable

import java.awt.Color
import java.awt.event.MouseEvent
import java.awt.event.MouseListener

class MyMouseListener implements MouseListener {
    @Override
    void mouseClicked(MouseEvent e) {

        //bilder och ytor ritas från övre vänstra hörnet

        Model.drawables.findAll { Drawable drawable ->
            e.point.x >= drawable.x - 30 && e.point.x <= drawable.x + 30 && e.point.y >= drawable.y - 30 && e.point.y <= drawable.y + 30
        }.each { Drawable drawable ->
            drawable.image = Model.shadeImage(Model.shapeProperties[drawable.shape].image, Color.lightGray)

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
