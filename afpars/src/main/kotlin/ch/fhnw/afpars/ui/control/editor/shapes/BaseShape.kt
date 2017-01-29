package ch.fhnw.afpars.ui.control.editor.shapes

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.paint.Paint

/**
 * Created by cansik on 29.01.17.
 */
abstract class BaseShape {
    var fill: Paint = Color.WHITE!!
    var stroke: Paint = Color.BLACK!!
    var strokeWeight = 1.0


    fun noFill() {
        fill = Color.TRANSPARENT
    }

    fun noStroke() {
        stroke = Color.TRANSPARENT
    }

    abstract fun render(gc: GraphicsContext)
}