package ch.fhnw.afpars.ui.control.editor.shapes

import ch.fhnw.afpars.geometry.Vector2
import ch.fhnw.afpars.util.format
import ch.fhnw.afpars.util.toCvPoint
import ch.fhnw.afpars.util.toCvScalar
import javafx.geometry.Dimension2D
import javafx.geometry.Point2D
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import org.jfree.graphics2d.svg.SVGGraphics2D
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.imgproc.Imgproc

/**
 * Created by cansik on 14.02.17.
 */
open class PolygonShape() : BaseShape() {
    var points = mutableListOf<Point2D>()

    constructor(points : MutableList<Point2D> = mutableListOf<Point2D>()) : this() {
        this.points = points
    }

    override fun render(gc: GraphicsContext) {
        gc.fillPolygon(points.map { it.x }.toDoubleArray(), points.map { it.y }.toDoubleArray(), points.size)
        gc.strokePolygon(points.map { it.x }.toDoubleArray(), points.map { it.y }.toDoubleArray(), points.size)
    }

    override fun renderToSvg(g: SVGGraphics2D) {
        super.renderToSvg(g)
        g.drawPolygon(points.map { it.x.toInt() }.toIntArray(), points.map { it.y.toInt() }.toIntArray(), points.size)
    }

    override fun renderToMat(m: Mat) {
        super.renderToMat(m)

        // not implemented
    }

    override fun toString(): String {
        return "Poly (${area().format(0)} px)"
    }

    fun area() : Double
    {
        return polygonArea(points.map { it.x }.toDoubleArray(), points.map { it.y }.toDoubleArray(), points.size)
    }

    private fun polygonArea(x : DoubleArray, y : DoubleArray, npoints : Int) : Double
    {
        var area = 0.0
        var j = npoints - 1
        var i = 0

        while(i < npoints)
        {
            area += (x[j]+x[i]) * (y[j]-y[i])
            j = i
            i++
        }

        return area / 2.0
    }

    /**
     * PNPoly Method:
     * http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html#The%20C%20Code
     */
    override fun contains(point: Point2D): Boolean {
        var inside = false
        var i = 0
        var j = points.size - 1
        while (i < points.size) {
            if (points[i].y > point.y != points[j].y > point.y
                    && point.x < (points[j].x - points[i].x)
                    * (point.y - points[i].y)
                    / (points[j].y - points[i].y) + points[i].x)
                inside = !inside
            j = i++
        }
        return inside
    }
}