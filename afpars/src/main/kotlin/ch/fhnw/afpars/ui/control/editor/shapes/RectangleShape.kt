package ch.fhnw.afpars.ui.control.editor.shapes

import javafx.geometry.Dimension2D
import javafx.geometry.Point2D
import javafx.scene.canvas.GraphicsContext

/**
 * Created by cansik on 29.01.17.
 */
class RectangleShape() : BaseShape() {
    var location = Point2D.ZERO!!
    var size = Dimension2D(5.0, 5.0)

    constructor(location: Point2D, size: Dimension2D) : this() {
        this.location = location
        this.size = size
    }

    override fun render(gc: GraphicsContext) {
        gc.fillRect(location.x, location.y, size.width, size.height)
        gc.strokeRect(location.x, location.y, size.width, size.height)
    }
}