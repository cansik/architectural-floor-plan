package ch.fhnw.afpars.ui.control.editor

import ch.fhnw.afpars.event.Event
import ch.fhnw.afpars.ui.control.editor.shapes.BaseShape
import ch.fhnw.afpars.ui.control.editor.shapes.RectangleShape
import ch.fhnw.afpars.ui.control.editor.tools.IEditorTool
import ch.fhnw.afpars.ui.control.editor.tools.ViewTool
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Dimension2D
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.input.ScrollEvent
import javafx.scene.input.ZoomEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.ImagePattern
import javafx.scene.shape.Rectangle


/**
 * Created by cansik on 25.01.17.
 */
class ImageEditor : Pane() {

    companion object
    {
        @JvmStatic
        val DRAW_LAYER_NAME = "draw"

        @JvmStatic
        val IMAGE_LAYER_NAME = "image"
    }

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

    var minimumZoom = 0.0
    var maximumZoom = 50.0

    val onShapeAdded = Event<Layer>()

    val scale: Double
        get() = relationScale + zoomScale

    init {
        children.add(canvas)

        // setup layer system
        layers.add(activeLayer)

        // make background gray
        style = "-fx-background-color: #696969;"

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

    fun addShape(shape: BaseShape) {
        activeLayer.shapes.add(shape)
        onShapeAdded(activeLayer)
    }

    fun resetZoom() {
        // call reset zoom twice
        resetZoomImp()
        resetZoomImp()
    }

    fun resetZoomImp() {
        canvasTransformation = Point2D.ZERO!!
        zoomTransformation = Point2D.ZERO!!

        zoomScale = minimumZoom
        relationScale = 1.0

        canvas.translateX = 0.0
        canvas.translateY = 0.0

        resize()
        zoom(canvas, 1.0, layoutX + zoomTransformation.x, layoutY + zoomTransformation.y)
    }

    private fun drawLayer(layer: Layer) {
        val gc = canvas.graphicsContext2D
        layer.shapes.filter { it.visible }.forEach {
            gc.fill = it.fill
            gc.stroke = it.stroke

            it.render(gc)

            if(it.marked)
            {
                gc.fill = it.markedFill
                gc.stroke = it.markedStroke

                it.render(gc)
            }
        }
    }

    fun displayImage(image: Image) {
        resizeCanvas(image.width, image.height)

        // set layer
        val imageLayer = Layer(IMAGE_LAYER_NAME)
        val drawLayer = Layer(DRAW_LAYER_NAME)

        addImage(imageLayer, image)

        layers.clear()
        layers.add(imageLayer)
        layers.add(drawLayer)
        activeLayer = drawLayer

        resetZoom()
        resize()
        redraw()
    }

    fun addImage(imageLayer : Layer, image: Image, visible : Boolean = true)
    {
        val imageRect = RectangleShape()
        imageRect.visible = visible
        imageRect.size = Dimension2D(image.width, image.height)
        imageRect.noStroke()
        imageRect.fill = ImagePattern(image, 0.0, 0.0, image.width, image.height, false)

        imageLayer.shapes.add(imageRect)
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
        if (scale < minimumZoom) scale = minimumZoom
        if (scale > maximumZoom) scale = maximumZoom

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