package ch.fhnw.afpars.ui.control.editor.shapes

import ch.fhnw.afpars.util.format
import ch.fhnw.afpars.util.toCvPoint
import ch.fhnw.afpars.util.toCvScalar
import javafx.geometry.Dimension2D
import javafx.geometry.Point2D
import javafx.scene.canvas.GraphicsContext
import org.jfree.graphics2d.svg.SVGGraphics2D
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

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

    override fun renderToSvg(g: SVGGraphics2D) {
        super.renderToSvg(g)
        g.fillRect(location.x.toInt(), location.y.toInt(), size.width.toInt(), size.height.toInt())
    }

    override fun renderToMat(m: Mat) {
        super.renderToMat(m)
        Imgproc.rectangle(m,
                location.toCvPoint(),
                (location.add(Point2D(size.width, size.height))).toCvPoint(),
                fill.toCvScalar(),
                -1)
    }

    override fun toString(): String {
        return "Rect (${location.x.format(1)} | ${location.y.format(1)}, w: ${size.width.format(1)}, h: ${size.height.format(1)})"
    }

    override fun contains(point: Point2D): Boolean {
        return location.x <= point.x
            && location.y <= point.y
            && point.x <= location.x + size.width
            && point.y <= location.y + size.height
    }
}