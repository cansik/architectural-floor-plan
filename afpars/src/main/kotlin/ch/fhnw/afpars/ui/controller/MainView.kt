package ch.fhnw.afpars.ui.controller

import ch.fhnw.afpars.algorithm.IAlgorithm
import ch.fhnw.afpars.algorithm.informationsegmentation.MorphologicalTransform
import ch.fhnw.afpars.algorithm.semanticanalysis.ConnectedComponentDetection
import ch.fhnw.afpars.algorithm.semanticanalysis.GapClosingAlgorithm
import ch.fhnw.afpars.algorithm.semanticanalysis.SimplifiedGapClosing
import ch.fhnw.afpars.algorithm.structuralanalysis.CascadeClassifierDetector
import ch.fhnw.afpars.algorithm.structuralanalysis.ExteriorWallClosing
import ch.fhnw.afpars.io.opencv.MatRender
import ch.fhnw.afpars.io.reader.AFImageReader
import ch.fhnw.afpars.io.svg.SvgRender
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.model.RoomPolygonShape
import ch.fhnw.afpars.ui.UITask
import ch.fhnw.afpars.ui.control.TagItem
import ch.fhnw.afpars.ui.control.editor.ImageEditor
import ch.fhnw.afpars.ui.control.editor.Layer
import ch.fhnw.afpars.ui.control.editor.shapes.BaseShape
import ch.fhnw.afpars.ui.control.editor.tools.LineTool
import ch.fhnw.afpars.ui.control.editor.tools.RectangleTool
import ch.fhnw.afpars.ui.control.editor.tools.RulerTool
import ch.fhnw.afpars.ui.control.editor.tools.ViewTool
import ch.fhnw.afpars.ui.items
import ch.fhnw.afpars.util.*
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
import org.opencv.core.Mat
import java.nio.file.Files


/**
 * Created by cansik on 15.01.17.
 */
class MainView {
    companion object
    {
        @JvmStatic
        val MAX_TEXTURE_SIZE = 2048
    }

    val image = SimpleObjectProperty<AFImage>()

    val statusText = SimpleObjectProperty<String>("Status")

    val canvas = ImageEditor()

    var workflowEngine = WorkflowEngine()

    val defaultWorkflow = Workflow(
            arrayListOf(
                    CascadeClassifierDetector(AFImage.DOOR_CASCADE,AFImage.DOOR_ATTRIBUTE_NAME),
                    MorphologicalTransform(),
                    ExteriorWallClosing(),
                    SimplifiedGapClosing(),
                    ConnectedComponentDetection()
            ).toTypedArray())

    val rulerTool = RulerTool()

    val viewTool = ViewTool()

    @FXML
    lateinit var layoutPane: BorderPane

    @FXML
    lateinit var layerTreeView: TreeView<TagItem>

    @FXML
    lateinit var runWorkflowButton: Button

    @FXML
    lateinit var nextStepButton: Button

    @FXML
    lateinit var cancelWorkflowButton: Button

    @FXML
    lateinit var loadFromClipBoardButton : Button

    @FXML
    lateinit var loadFromFileButton : Button

    @FXML
    lateinit var progressIndicator : ProgressIndicator

    @FXML
    lateinit var statusLabel : Label

    @FXML
    lateinit var breadCrumbLabel : Label

    @FXML
    lateinit var breadCrumbBox: ChoiceBox<String>

    @FXML
    lateinit var roomSizeLabel : Label

    init {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

        image.addListener {
            o ->
            imageUpdated()
        }

        workflowEngine.finished += {
            println("workflow finished!")
            image.set(it)
            imageUpdated()
            setWorkflowStopMode()
        }

        workflowEngine.stepDone += {
            val algorithm = it.first
            val img = it.second

            updateBreadCrump(defaultWorkflow.algorithms[defaultWorkflow.algorithms.indexOfFirst { it == algorithm } + 1])

            println("${algorithm.name} finished!")
            image.set(img)
            imageUpdated()

            progressIndicator.isVisible = false
        }

        // on change, edit all polygons in layers
        rulerTool.pixelLength.addListener { o ->
            canvas.layers.forEach { l ->
                l.shapes.filterIsInstance<RoomPolygonShape>().forEach {
                    it.relation = rulerTool.pixelLength.value
                }
            }

            updateLayers()

            canvas.activeTool = viewTool
        }

        // select shape if one is selected
        viewTool.shapesSelected += { shapes ->
            val items = layerTreeView.items()
            val treeViewItem = items.filter { shapes.filter{ s -> it.value.item == s}.isNotEmpty() }.first()
            layerTreeView.selectionModel.select(treeViewItem)
        }
    }

    fun setupView() {
        UITask.run({

            // init canvas
            canvas.prefWidth(100.0)
            canvas.prefWidth(100.0)

            layoutPane.center = canvas

            canvas.onShapeAdded += { updateUI() }

            // setup buttons
            runWorkflowButton.managedProperty().bind(runWorkflowButton.visibleProperty())
            breadCrumbBox.managedProperty().bind(breadCrumbBox.visibleProperty())
            breadCrumbLabel.managedProperty().bind(breadCrumbLabel.visibleProperty())
            nextStepButton.managedProperty().bind(nextStepButton.visibleProperty())
            cancelWorkflowButton.managedProperty().bind(cancelWorkflowButton.visibleProperty())

            // setup treeview
            layerTreeView.selectionModel.selectedItemProperty().addListener { o -> markSelectedItem() }

            // setup task model
            UITask.status.addListener { o -> statusLabel.text = UITask.status.value }
            UITask.running.addListener { o -> progressIndicator.isVisible = UITask.running.value }

            setWorkflowStopMode()

        }, {
            updateUI()
        },
                "UI Setup")
    }

    fun imageUpdated() {
        Platform.runLater({
            val afImage = image.value
            canvas.displayImage(afImage.image.toImage())

            // also show original image
            if(afImage.attributes.containsKey(AFImageReader.ORIGINAL_IMAGE))
                canvas.addImage(canvas.layers.single{it.name == ImageEditor.IMAGE_LAYER_NAME},
                        afImage.attributes[AFImageReader.ORIGINAL_IMAGE]!!.toImage(), false)

            // show layers
            for ((name, shapes) in afImage.layers) {
                val layer = Layer(name)
                layer.shapes.addAll(shapes)
                canvas.layers.add(1, layer)
            }

            canvas.activeTool = viewTool
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
        progressIndicator.isVisible = true
        MatRender.render(workflowEngine.currentImage.image, canvas.activeLayer.shapes)
        workflowEngine.nextStep()
    }

    fun updateLayers() {
        val rootItem = CheckBoxTreeItem(TagItem(name = "layers"))
        rootItem.isExpanded = true

        layerTreeView.isShowRoot = false
        layerTreeView.isEditable = true
        layerTreeView.cellFactory = CheckBoxTreeCell.forTreeView()

        for (layer in canvas.layers.reversed()) {
            val layerItem = CheckBoxTreeItem(TagItem(item = layer))
            layerItem.isSelected = layer.visible
            layerItem.selectedProperty().addListener { o ->
                run {
                    layer.visible = layerItem.isSelected
                    canvas.redraw()
                }
            }
            rootItem.children.add(layerItem)

            for (shape in layer.shapes) {
                val shapeItem = CheckBoxTreeItem(TagItem(item = shape))
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

        layerTreeView.root = rootItem
        displayTotalRoomSize()
    }

    fun updateUI() {
        updateLayers()
    }

    fun loadImageFromClipBoard(e: ActionEvent) {
        val cb = Clipboard.getSystemClipboard()
        if (cb.hasImage()) {
            val afImg = AFImage(cb.image.toMat())
            loadImage(afImg)
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
            UITask.run({loadImage(AFImageReader().read(result.toPath()))}, taskName = "load image")
        }
    }

    fun loadImage(afImg : AFImage)
    {
        // resize if needed
        if(afImg.image.width() > MAX_TEXTURE_SIZE || afImg.image.height() > MAX_TEXTURE_SIZE) {
            var nimg = Mat()

            if (afImg.image.width() > afImg.image.height())
                nimg = afImg.image.resize(MAX_TEXTURE_SIZE, 0)
            else
                nimg = afImg.image.resize(0, MAX_TEXTURE_SIZE)

            afImg.image.release()
            afImg.image = nimg
        }

        image.set(afImg)
        afImg.attributes.put( AFImageReader.ORIGINAL_IMAGE, afImg.image.copy())
        statusText.set("image ${afImg.name} loaded!")
        updateUI()
    }

    fun toolChanged(e: ActionEvent) {
        when ((e.source as Button).id) {
            "arrowButton" -> canvas.activeTool = viewTool
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

    fun exportToCSV(e : ActionEvent)
    {
        if(canvas.layers.singleOrNull { it.name ==  ConnectedComponentDetection.ROOM_LAYER_NAME} == null)
            return

        val stage = (e.source as Node).scene.window as Stage

        val fileChooser = FileChooser()
        fileChooser.initialFileName = "rooms.csv"
        fileChooser.title = "Export rooms as csv"
        fileChooser.extensionFilters.addAll(FileChooser.ExtensionFilter("Comma-Separated Values", "*.csv"))

        val file = fileChooser.showSaveDialog(stage)

        if (file != null) {
            val seperator = ";"
            val b = StringBuilder()

            // write header
            b.append("id${seperator}name${seperator}size${seperator}pixel")
            b.append(System.lineSeparator())

            // write content
            val rooms = canvas.layers.single { it.name ==  ConnectedComponentDetection.ROOM_LAYER_NAME}.shapes
            rooms.filterIsInstance<RoomPolygonShape>().forEachIndexed { i, room ->
                b.append("$i$seperator$room$seperator${room.areaInCentimeter()}$seperator${room.area()}")
                b.append(System.lineSeparator())
            }

            Files.write(file.toPath(), listOf(b.toString()))
        }
    }

    fun removeItem(e : ActionEvent)
    {
        removeSelectedItem()
    }

    private fun updateBreadCrump(currentAlgorithm: IAlgorithm) {
        Platform.runLater({
            breadCrumbBox.items.clear()
            breadCrumbBox.items.addAll(defaultWorkflow.algorithms.map { it.name })
            breadCrumbBox.selectionModel.select(currentAlgorithm.name)
        })
    }

    private fun setWorkflowRunningMode() {
        Platform.runLater({
            runWorkflowButton.isVisible = false

            breadCrumbBox.isVisible = true
            breadCrumbLabel.isVisible = true
            nextStepButton.isVisible = true
            cancelWorkflowButton.isVisible = true

            loadFromFileButton.isDisable = true
            loadFromClipBoardButton.isDisable = true

            progressIndicator.isVisible = true
        })
    }

    private fun setWorkflowStopMode() {
        Platform.runLater({
            runWorkflowButton.isVisible = true

            breadCrumbBox.isVisible = false
            breadCrumbLabel.isVisible = false
            nextStepButton.isVisible = false
            cancelWorkflowButton.isVisible = false

            loadFromFileButton.isDisable = false
            loadFromClipBoardButton.isDisable = false

            progressIndicator.isVisible = false
        })
    }

    private fun removeSelectedItem()
    {
        if(layerTreeView.selectionModel.selectedItem == null)
            return

        val item = layerTreeView.selectionModel.selectedItem

        // delete only if is leave
        if(item.parent.value.item is Layer)
        {
            when((item.parent.value.item as Layer).name)
            {
                ImageEditor.DRAW_LAYER_NAME -> canvas.layers
                        .single { it.name == ImageEditor.DRAW_LAYER_NAME }
                        .shapes.remove(item.value.item as BaseShape)

                ConnectedComponentDetection.ROOM_LAYER_NAME -> canvas.layers
                        .single { it.name == ConnectedComponentDetection.ROOM_LAYER_NAME }
                        .shapes.remove(item.value.item as RoomPolygonShape)
            }
        }

        updateLayers()
        canvas.redraw()
    }

    private fun markSelectedItem()
    {
        if(layerTreeView.selectionModel.selectedItem == null)
            return

        val item = layerTreeView.selectionModel.selectedItem

        // select only if is leave
        if(item.parent.value.item is Layer)
        {
            val shape = item.value.item as BaseShape

            // deselected all shapes
            canvas.layers.forEach { l -> l.shapes.forEach { s -> s.marked = false } }
            shape.marked = true

            canvas.redraw()
        }
    }

    private fun displayTotalRoomSize()
    {
        if(canvas.layers.singleOrNull { it.name ==  ConnectedComponentDetection.ROOM_LAYER_NAME} == null) {
            roomSizeLabel.text = ""
            return
        }

        val rooms = canvas.layers.single { it.name ==  ConnectedComponentDetection.ROOM_LAYER_NAME}.shapes
        roomSizeLabel.text = "Total: ${(rooms.map {(it as RoomPolygonShape).areaInCentimeter() }.sum() / 10000.0).format(2)} m²"
    }
}