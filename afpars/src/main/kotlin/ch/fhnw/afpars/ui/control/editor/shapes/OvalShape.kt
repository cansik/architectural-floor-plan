package ch.fhnw.afpars.ui.control.editor.shapes

import javafx.geometry.Dimension2D
import javafx.geometry.Point2D
import javafx.scene.canvas.GraphicsContext
import org.jfree.graphics2d.svg.SVGGraphics2D
import org.opencv.core.Mat

/**
 * Created by cansik on 29.01.17.
 */
open class OvalShape() : BaseShape() {
    var location = Point2D.ZERO!!
    var size = Dimension2D(5.0, 5.0)

    constructor(center: Point2D, size: Dimension2D) : this() {
        this.location = center
        this.size = size
    }

    override fun render(gc: GraphicsContext) {
        gc.fillOval(location.x, location.y, size.width, size.height)
        gc.strokeOval(location.x, location.y, size.width, size.height)
    }

    override fun renderToSvg(g: SVGGraphics2D) {
        super.renderToSvg(g)
        g.fillOval(location.x.toInt(), location.y.toInt(), size.width.toInt(), size.height.toInt())
    }

    override fun renderToMat(m: Mat) {
        super.renderToMat(m)

        //todo: implement ellipse drawing in opencv

        //Imgproc.ellipse(m, location.toCvPoint(), size.toCvSize(), fill.toCvScalar())
    }
}