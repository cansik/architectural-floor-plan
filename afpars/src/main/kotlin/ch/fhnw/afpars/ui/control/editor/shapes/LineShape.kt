package ch.fhnw.afpars.ui.control.editor.shapes

import ch.fhnw.afpars.util.format
import ch.fhnw.afpars.util.toCvPoint
import ch.fhnw.afpars.util.toCvScalar
import javafx.geometry.Point2D
import javafx.scene.canvas.GraphicsContext
import org.jfree.graphics2d.svg.SVGGraphics2D
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

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

    override fun renderToSvg(g: SVGGraphics2D) {
        super.renderToSvg(g)
        g.drawLine(point1.x.toInt(), point1.y.toInt(), point2.x.toInt(), point2.y.toInt())
    }

    override fun renderToMat(m: Mat) {
        super.renderToMat(m)
        Imgproc.line(m, point1.toCvPoint(), point2.toCvPoint(), stroke.toCvScalar(), strokeWeight.toInt())
    }

    override fun toString(): String {
        return "Line (${point1.x.format(1)} | ${point1.y.format(1)}, ${point2.x.format(1)} | ${point2.y.format(1)})"
    }

    override fun contains(point: Point2D): Boolean {
        val x1 = point1.x
        val x2 = point2.x
        val y1 = point1.y
        val y2 = point2.y

        val x = point.x
        val y = point.y

        val AB = Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1))
        val AP = Math.sqrt((x-x1)*(x-x1)+(y-y1)*(y-y1))
        val PB = Math.sqrt((x2-x)*(x2-x)+(y2-y)*(y2-y))
        return AB == AP + PB
    }
}