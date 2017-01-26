package ch.fhnw.afpars.ui.control

import ch.fhnw.afpars.ui.control.tools.ViewTool
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.canvas.Canvas
import javafx.scene.input.ScrollEvent
import javafx.scene.input.ZoomEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle


/**
 * Created by cansik on 25.01.17.
 */
class ImageEditor : Pane() {
    val canvas = Canvas(600.0, 400.0)
    val outputClip = Rectangle()

    var activeTool = ViewTool()

    // calculated value
    private var relationScale = 1.0

    // basic view controls
    var zoomScale = 0.0
    var canvasTransformation = Point2D.ZERO!!

    val scale: Double
        get() = relationScale + zoomScale

    init {
        children.add(canvas)

        val gc = canvas.graphicsContext2D
        gc.fill = Color.LIGHTGRAY
        gc.fillRect(0.0, 0.0, canvas.width, canvas.height)

        gc.fill = Color.CORNFLOWERBLUE
        gc.fillOval(10.0, 60.0, 500.0, 500.0)

        gc.stroke = Color.GREENYELLOW
        gc.strokeRect(0.0, 0.0, canvas.width, canvas.height)

        // setup resize
        widthProperty().addListener { o -> resize() }
        heightProperty().addListener { o -> resize() }

        // setup clipping
        clip = outputClip

        layoutBoundsProperty().addListener({ ov, oldValue, newValue ->
            outputClip.width = newValue.width
            outputClip.height = newValue.height
        })

        // tool listeners
        // canvas
        canvas.setOnMouseClicked { event -> activeTool.onCanvasMouseClicked(this, event) }

        canvas.setOnMousePressed { event -> activeTool.onCanvasMousePressed(this, event) }
        canvas.setOnMouseReleased { event -> activeTool.onCanvasMouseReleased(this, event) }
        canvas.setOnMouseDragged { event -> activeTool.onCanvasMouseDragged(this, event) }

        canvas.setOnScroll { event -> activeTool.onCanvasScroll(this, event) }

        canvas.setOnKeyPressed { event -> activeTool.onCanvasKeyPressed(this, event) }

        // pane
        setOnMouseClicked { event -> activeTool.onEditorMouseClicked(this, event) }

        setOnMousePressed { event -> activeTool.onEditorMousePressed(this, event) }
        setOnMouseReleased { event -> activeTool.onEditorMouseReleased(this, event) }
        setOnMouseDragged { event -> activeTool.onEditorMouseDragged(this, event) }

        setOnScroll { event -> activeTool.onEditorScroll(this, event) }

        setOnKeyPressed { event -> activeTool.onEditorKeyPressed(this, event) }
    }

    fun resize() {
        // calculate the relationScale
        if (width - canvas.width * scale > height - canvas.height * scale) {
            relationScale = height / canvas.height
        } else {
            relationScale = width / canvas.width
        }

        zoom(canvas, scale, layoutX, layoutY)

        canvas.translateX += canvasTransformation.x
        canvas.translateY += canvasTransformation.y
    }

    /** Allow to zoom/relationScale any node with pivot at scene (x,y) coordinates.

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


        // fix relationScale
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