package ch.fhnw.afpars.ui.control.editor.shapes

import javafx.geometry.Point2D
import javafx.scene.canvas.GraphicsContext

/**
 * Created by cansik on 29.01.17.
 */
class LineShape() : BaseShape() {
    var point1 = Point2D.ZERO!!
    var point2 = Point2D.ZERO!!

    constructor(point1: Point2D, point2: Point2D) : this() {
        this.point1 = point1
        this.point2 = point2
    }

    override fun render(gc: GraphicsContext) {
        gc.strokeLine(point1.x, point1.y, point2.x, point2.y)
    }
}