package ch.fhnw.afpars.ui.controller

import ch.fhnw.afpars.algorithm.IAlgorithm
import ch.fhnw.afpars.algorithm.informationsegmentation.MorphologicalTransform
import ch.fhnw.afpars.algorithm.semanticanalysis.ConnectedComponentDetection
import ch.fhnw.afpars.algorithm.semanticanalysis.NikieRoomDetection
import ch.fhnw.afpars.algorithm.structuralanalysis.CascadeClassifierDetector
import ch.fhnw.afpars.io.opencv.MatRender
import ch.fhnw.afpars.io.reader.AFImageReader
import ch.fhnw.afpars.io.svg.SvgRender
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.ui.control.editor.ImageEditor
import ch.fhnw.afpars.ui.control.editor.Layer
import ch.fhnw.afpars.ui.control.editor.tools.LineTool
import ch.fhnw.afpars.ui.control.editor.tools.RectangleTool
import ch.fhnw.afpars.ui.control.editor.tools.RulerTool
import ch.fhnw.afpars.ui.control.editor.tools.ViewTool
import ch.fhnw.afpars.util.toImage
import ch.fhnw.afpars.util.toMat
import ch.fhnw.afpars.workflow.Workflow
import ch.fhnw.afpars.workflow.WorkflowEngine
import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.control.cell.CheckBoxTreeCell
import javafx.scene.input.Clipboard
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.opencv.core.Core
import java.nio.file.Files


/**
 * Created by cansik on 15.01.17.
 */
class MainView {
    val image = SimpleObjectProperty<AFImage>()

    val statusText = SimpleObjectProperty<String>("Status")

    val canvas = ImageEditor()

    var workflowEngine = WorkflowEngine()

    val defaultWorkflow = Workflow(
            arrayListOf(
                    MorphologicalTransform(),
                    CascadeClassifierDetector(),
                    NikieRoomDetection(),
                    ConnectedComponentDetection()
            ).toTypedArray())

    val rulerTool = RulerTool()

    @FXML
    var layoutPane: BorderPane? = null

    @FXML
    var layerTreeView: TreeView<String>? = null

    @FXML
    var runWorkflowButton: Button? = null

    @FXML
    var nextStepButton: Button? = null

    @FXML
    var cancelWorkflowButton: Button? = null

    @FXML
    var breadCrumbLabel: Label? = null

    init {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

        image.addListener {
            o ->
            imageUpdated()
        }

        workflowEngine.finished += {
            println("workflow finished!")
            image.set(it)
            setWorkflowStopMode()
        }

        workflowEngine.stepDone += {
            val algorithm = it.first
            val img = it.second

            updateBreadCrump(defaultWorkflow.algorithms[defaultWorkflow.algorithms.indexOfFirst { it == algorithm } + 1])

            println("${algorithm.name} finished!")
            image.set(img)
            imageUpdated()
        }
    }

    fun setupView() {
        // init canvas
        canvas.prefWidth(100.0)
        canvas.prefWidth(100.0)

        layoutPane!!.center = canvas

        canvas.onShapeAdded += { updateUI() }

        // setup buttons
        runWorkflowButton!!.managedProperty().bind(runWorkflowButton!!.visibleProperty())
        breadCrumbLabel!!.managedProperty().bind(breadCrumbLabel!!.visibleProperty())
        nextStepButton!!.managedProperty().bind(nextStepButton!!.visibleProperty())
        cancelWorkflowButton!!.managedProperty().bind(cancelWorkflowButton!!.visibleProperty())

        setWorkflowStopMode()

        updateUI()
    }

    fun imageUpdated() {
        Platform.runLater({
            val afImage = image.value
            canvas.displayImage(afImage.image.toImage())

            // show layers
            for ((name, shapes) in afImage.layers) {
                val layer = Layer(name)
                layer.shapes.addAll(shapes)
                canvas.layers.add(1, layer)
            }

            updateLayers()
            canvas.redraw()
        })
    }

    fun runWorkflow(e: ActionEvent) {
        if (canvas.layers.size < 2) {
            println("Please load a picture first!")
            return
        }

        setWorkflowRunningMode()
        updateBreadCrump(defaultWorkflow.algorithms.first())

        MatRender.render(image.value.image, canvas.activeLayer.shapes)
        workflowEngine.run(defaultWorkflow, image.value, true, true)
    }

    fun nextStep(e: ActionEvent) {
        MatRender.render(workflowEngine.currentImage.image, canvas.activeLayer.shapes)
        workflowEngine.nextStep()
    }

    fun updateLayers() {
        val rootItem = CheckBoxTreeItem("layers")
        rootItem.isExpanded = true

        layerTreeView!!.isShowRoot = false
        layerTreeView!!.isEditable = true
        layerTreeView!!.cellFactory = CheckBoxTreeCell.forTreeView()

        for (layer in canvas.layers.reversed()) {
            val layerItem = CheckBoxTreeItem("${layer.name} (${layer.shapes.size} Items)")
            layerItem.isSelected = layer.visible
            layerItem.selectedProperty().addListener { o ->
                run {
                    layer.visible = layerItem.isSelected
                    canvas.redraw()
                }
            }
            rootItem.children.add(layerItem)

            for (shape in layer.shapes) {
                val shapeItem = CheckBoxTreeItem("$shape")
                shapeItem.isSelected = layer.visible
                shapeItem.selectedProperty().addListener { o ->
                    run {
                        shape.visible = shapeItem.isSelected
                        canvas.redraw()
                    }
                }

                layerItem.children.add(shapeItem)
            }
        }

        layerTreeView!!.root = rootItem
    }

    fun updateUI() {
        updateLayers()
    }

    fun loadImageFromClipBoard(e: ActionEvent) {
        val cb = Clipboard.getSystemClipboard()
        if (cb.hasImage()) {
            image.set(AFImage(cb.image.toMat()))
            updateUI()
        }
    }

    fun loadImageFromFile(e: ActionEvent) {
        val stage = (e.source as Node).scene.window as Stage

        val fileChooser = FileChooser()
        fileChooser.title = "Select image to process"
        fileChooser.initialFileName = ""
        fileChooser.extensionFilters.addAll(
                FileChooser.ExtensionFilter("All Images", "*.jpg", "*.jpeg", "*.png"),
                FileChooser.ExtensionFilter("JPG", "*.jpg", "*.jpeg"),
                FileChooser.ExtensionFilter("PNG", "*.png")
        )

        val result = fileChooser.showOpenDialog(stage)

        if (result != null) {
            image.set(AFImageReader().read(result.toPath()))
            statusText.set("image ${result.name} loaded!")
            updateUI()
        }
    }

    fun toolChanged(e: ActionEvent) {
        when ((e.source as Button).id) {
            "arrowButton" -> canvas.activeTool = ViewTool()
            "rulerButton" -> canvas.activeTool = rulerTool
            "lineButton" -> {
                val tool = LineTool()
                tool.defaultStroke = Color.BLACK
                tool.defaultStrokeWeight = 2.0
                canvas.activeTool = tool
            }
            "rectButton" -> {
                val tool = RectangleTool()
                tool.defaultFill = Color.BLACK
                tool.defaultStroke = Color.TRANSPARENT
                canvas.activeTool = tool
            }
            "eraseButton" -> {
                val tool = RectangleTool()
                tool.defaultFill = Color.WHITE
                tool.defaultStroke = Color.TRANSPARENT
                canvas.activeTool = tool
            }
        }
    }

    fun cancelWorkflow(e: ActionEvent) {
        workflowEngine.cancelRequested = true
        workflowEngine.nextStep()
    }

    fun exportLayer(e: ActionEvent) {
        val stage = (e.source as Node).scene.window as Stage

        // show selection dialog
        val dialog = ChoiceDialog(canvas.layers.first().name, canvas.layers.map { it.name })
        dialog.title = "Layer Export"
        dialog.headerText = "Export a layer from the canvas as svg image."
        dialog.contentText = "Choose the layer to export:"

        val result = dialog.showAndWait()

        result.ifPresent({ layerName ->
            val layer = canvas.layers.single { it.name == layerName }
            val fileChooser = FileChooser()
            fileChooser.initialFileName = "${layerName.toLowerCase()}.svg"
            fileChooser.title = "Export layer \"$layerName\" as svg"
            fileChooser.extensionFilters.addAll(FileChooser.ExtensionFilter("Vector Graphic", "*.svg"))

            val file = fileChooser.showSaveDialog(stage)

            if (file != null) {
                val svg = SvgRender.render(
                        canvas.canvas.width.toInt(),
                        canvas.canvas.height.toInt(),
                        layer.shapes)

                Files.write(file.toPath(), listOf(svg.svgDocument))
            }
        })
    }

    private fun updateBreadCrump(currentAlgorithm: IAlgorithm) {
        Platform.runLater({
            breadCrumbLabel!!.text = defaultWorkflow.algorithms.joinToString { (if (it == currentAlgorithm) "!${it.name}!" else it.name) + " > " }
        })
    }

    private fun setWorkflowRunningMode() {
        Platform.runLater({
            runWorkflowButton!!.isVisible = false

            breadCrumbLabel!!.isVisible = true
            nextStepButton!!.isVisible = true
            cancelWorkflowButton!!.isVisible = true
        })
    }

    private fun setWorkflowStopMode() {
        Platform.runLater({
            runWorkflowButton!!.isVisible = true

            breadCrumbLabel!!.isVisible = false
            nextStepButton!!.isVisible = false
            cancelWorkflowButton!!.isVisible = false

        })
    }
}