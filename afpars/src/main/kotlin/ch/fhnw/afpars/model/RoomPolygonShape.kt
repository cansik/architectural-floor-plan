package ch.fhnw.afpars.model

import ch.fhnw.afpars.ui.control.editor.shapes.PolygonShape
import ch.fhnw.afpars.util.format
import ch.fhnw.afpars.util.opencv.contour.Contour
import javafx.geometry.Point2D

/**
 * Created by cansik on 15.02.17.
 */
class RoomPolygonShape(val contour: Contour, points: MutableList<Point2D> = mutableListOf<Point2D>()) : PolygonShape(points) {
    var relation : Double = 0.0

    fun areaInCentimeter() : Double
    {
        return area() * Math.pow(relation, 2.0)
    }

    override fun toString(): String {
        if(relation > 0.0)
            return "Room (${(areaInCentimeter() / 10000.0).format(2)} m²)"
        else
            return "Room (${area()} px)"
    }
}