package ch.fhnw.afpars.ui.control

import ch.fhnw.afpars.ui.control.tools.ViewTool
import javafx.scene.Node
import javafx.scene.canvas.Canvas
import javafx.scene.input.ScrollEvent
import javafx.scene.input.ZoomEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color


/**
 * Created by cansik on 25.01.17.
 */
class ImageEditor : Pane() {
    val canvas = Canvas(800.0, 500.0)

    var activeTool = ViewTool()

    var scale = 1.0

    var zoomLevel = 0.0

    init {
        children.add(canvas)

        val gc = canvas.graphicsContext2D
        gc.fill = Color.YELLOW
        gc.fillRect(0.0, 0.0, canvas.width, canvas.height)

        gc.fill = Color.CORNFLOWERBLUE
        gc.fillOval(10.0, 60.0, 500.0, 500.0)

        gc.stroke = Color.GREENYELLOW
        gc.strokeRect(0.0, 0.0, canvas.width, canvas.height)

        widthProperty().addListener { o -> resize() }
        heightProperty().addListener { o -> resize() }

        canvas.setOnMouseClicked { event -> activeTool.onMouseClicked(this, event) }
    }

    fun resize() {
        // calculate the scale
        if (width - canvas.width * scale > height - canvas.height * scale) {
            scale = height / canvas.height
        } else {
            scale = width / canvas.width
        }

        val finalScale = scale + zoomLevel

        zoom(canvas, finalScale, layoutX, layoutY)
        //canvas.clip = Rectangle(layoutX, layoutY, width, height)
    }

    /** Allow to zoom/scale any node with pivot at scene (x,y) coordinates.

     * @param node
     * *
     * @param delta
     * *
     * @param x
     * *
     * @param y
     */
    fun zoom(node: Node, factor: Double, x: Double, y: Double) {
        val oldScale = node.scaleX
        var scale = factor


        // fix scale
        if (scale < 0.05) scale = 0.05
        if (scale > 50) scale = 50.0


        node.scaleX = scale
        node.scaleY = scale

        val f = scale / oldScale - 1
        val bounds = node.localToScene(node.boundsInLocal)
        val dx = x - (bounds.width / 2 + bounds.minX)
        val dy = y - (bounds.height / 2 + bounds.minY)

        node.translateX = node.translateX - f * dx
        node.translateY = node.translateY - f * dy
    }

    fun zoom(node: Node, event: ScrollEvent) {
        zoom(node, Math.pow(1.01, event.deltaY), event.sceneX, event.sceneY)
    }

    fun zoom(node: Node, event: ZoomEvent) {
        zoom(node, event.zoomFactor, event.sceneX, event.sceneY)
    }
}