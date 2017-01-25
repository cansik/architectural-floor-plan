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
    val canvas = Canvas(800.0, 500.0)

    var activeTool = ViewTool()

    var sizeFactor = 1.0

    init {
        children.add(canvas)

        val gc = canvas.graphicsContext2D
        gc.fill = Color.CORNFLOWERBLUE
        gc.fillOval(10.0, 60.0, 500.0, 500.0)

        gc.stroke = Color.GREENYELLOW
        gc.strokeRect(0.0, 0.0, canvas.width, canvas.height)

        widthProperty().addListener { o -> resize() }

        setOnMouseClicked { event -> activeTool.onMouseClicked(this, event) }
    }

    fun resize() {
        canvas.clip = Rectangle(width, height)

        if (canvas.width < canvas.height)
            sizeFactor = height / canvas.height
        else
            sizeFactor = width / canvas.width

        println("SizeFactor: $sizeFactor")

        canvas.scaleX = sizeFactor
        canvas.translateX = 0.0 + (width * (sizeFactor / 2.0)) - (width / 2.0)
    }
}