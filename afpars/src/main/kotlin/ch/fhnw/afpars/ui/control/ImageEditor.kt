package ch.fhnw.afpars.ui.control

import ch.fhnw.afpars.ui.control.tools.ViewTool
import javafx.scene.canvas.Canvas
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle


/**
 * Created by cansik on 25.01.17.
 */
class ImageEditor : Pane() {
    val canvas = Canvas(300.0, 300.0)

    var activeTool = ViewTool()

    init {
        children.add(canvas)

        val gc = canvas.graphicsContext2D
        gc.fill = Color.CORNFLOWERBLUE
        gc.fillOval(10.0, 60.0, 500.0, 500.0)

        widthProperty().addListener { o -> resize() }

        setOnMouseClicked { event -> activeTool.onMouseClicked(this, event) }
    }

    fun resize() {
        canvas.clip = Rectangle(width, height)
    }
}