package ch.fhnw.afpars.ui.control.editor.shapes

import ch.fhnw.afpars.io.svg.ISvgRenderable
import ch.fhnw.afpars.util.toAWT
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import org.jfree.graphics2d.svg.SVGGraphics2D

/**
 * Created by cansik on 29.01.17.
 */
abstract class BaseShape : ISvgRenderable {
    var fill: Paint = Color.WHITE!!
    var stroke: Paint = Color.BLACK!!
    var strokeWeight = 1.0


    fun noFill() {
        fill = Color.TRANSPARENT
    }

    fun noStroke() {
        stroke = Color.TRANSPARENT
    }

    override fun renderToSvg(g: SVGGraphics2D) {
        // prepare drawing
        if (fill is Color && fill != Color.TRANSPARENT) {
            g.paint = (fill as Color).toAWT()
        }

        if (stroke is Color && stroke != Color.TRANSPARENT) {
            g.paint = (stroke as Color).toAWT()
        }
    }

    abstract fun render(gc: GraphicsContext)
}