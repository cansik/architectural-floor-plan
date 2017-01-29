package ch.fhnw.afpars.ui.control.editor

import ch.fhnw.afpars.ui.control.editor.tools.IEditorTool
import ch.fhnw.afpars.ui.control.editor.tools.ViewTool
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.input.ScrollEvent
import javafx.scene.input.ZoomEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle


/**
 * Created by cansik on 25.01.17.
 */
class ImageEditor : Pane() {
    var canvas = ResizableCanvas(600.0, 400.0)
    val outputClip = Rectangle()

    private val activeToolProperty = SimpleObjectProperty<IEditorTool>(ViewTool())

    var activeTool: IEditorTool
        get() = activeToolProperty.value
        set(value) = activeToolProperty.set(value)

    val layers = mutableListOf<Layer>()

    var activeLayer: Layer = Layer("Background")

    // calculated value
    private var relationScale = 1.0

    // basic view controls
    var zoomScale = 0.0
    var canvasTransformation = Point2D.ZERO!!
    var zoomTransformation = Point2D.ZERO!!

    val scale: Double
        get() = relationScale + zoomScale

    init {
        children.add(canvas)

        // setup layer system
        layers.add(activeLayer)

        // draw default graphics
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

        // set cursor
        cursor = activeTool.cursor
        activeToolProperty.addListener { o -> cursor = activeTool.cursor }

        // tool listeners
        // canvas
        canvas.setOnMouseClicked { event -> activeTool.onCanvasMouseClicked(this, event) }

        canvas.setOnMousePressed { event -> activeTool.onCanvasMousePressed(this, event) }
        canvas.setOnMouseReleased { event -> activeTool.onCanvasMouseReleased(this, event) }
        canvas.setOnMouseDragged { event -> activeTool.onCanvasMouseDragged(this, event) }
        canvas.setOnMouseMoved { event -> activeTool.onCanvasMouseMoved(this, event) }

        canvas.setOnScroll { event -> activeTool.onCanvasScroll(this, event) }

        canvas.setOnKeyPressed { event -> activeTool.onCanvasKeyPressed(this, event) }

        // pane
        setOnMouseClicked { event -> activeTool.onEditorMouseClicked(this, event) }

        setOnMousePressed { event -> activeTool.onEditorMousePressed(this, event) }
        setOnMouseReleased { event -> activeTool.onEditorMouseReleased(this, event) }
        setOnMouseDragged { event -> activeTool.onEditorMouseDragged(this, event) }
        setOnMouseMoved { event -> activeTool.onEditorMouseMoved(this, event) }

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

        zoom(canvas, scale, layoutX + zoomTransformation.x, layoutY + zoomTransformation.y)

        canvas.translateX += canvasTransformation.x
        canvas.translateY += canvasTransformation.y
    }

    fun resizeCanvas(width: Double, height: Double) {
        // reset transformation
        resetZoom()

        // resize
        canvas.resize(width, height)

        // apply transformation
        resize()
    }

    fun redraw() {
        val gc = canvas.graphicsContext2D

        // clear canvas
        gc.clearRect(0.0, 0.0, canvas.width, canvas.height)

        // draw layers if they are visible
        layers.filter { it.visible }.forEach { drawLayer(it) }
    }

    fun resetZoom() {
        canvasTransformation = Point2D.ZERO
        zoomTransformation = Point2D.ZERO

        canvas.translateX = 0.0
        canvas.translateY = 0.0

        zoom(canvas, 1.0, layoutX + zoomTransformation.x, layoutY + zoomTransformation.y)
    }

    private fun drawLayer(layer: Layer) {
        val gc = canvas.graphicsContext2D
        layer.shapes.forEach {
            gc.fill = it.fill
            gc.stroke = it.stroke

            it.render(gc)
        }
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