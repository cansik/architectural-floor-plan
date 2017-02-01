package ch.fhnw.afpars.ui.controller

import ch.fhnw.afpars.algorithm.structuralanalysis.CascadeClassifierDetector
import ch.fhnw.afpars.algorithm.structuralanalysis.ShapeDistanceMatching
import ch.fhnw.afpars.io.reader.AFImageReader
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.ui.control.editor.ImageEditor
import ch.fhnw.afpars.ui.control.editor.tools.LineTool
import ch.fhnw.afpars.ui.control.editor.tools.RectangleTool
import ch.fhnw.afpars.ui.control.editor.tools.ViewTool
import ch.fhnw.afpars.util.toImage
import ch.fhnw.afpars.util.toMat
import ch.fhnw.afpars.workflow.Workflow
import ch.fhnw.afpars.workflow.WorkflowEngine
import javafx.beans.property.SimpleObjectProperty
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.CheckBoxTreeItem
import javafx.scene.control.TreeView
import javafx.scene.control.cell.CheckBoxTreeCell
import javafx.scene.input.Clipboard
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.opencv.core.Core


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
                    CascadeClassifierDetector(),
                    ShapeDistanceMatching()
            ).toTypedArray())

    @FXML
    var layoutPane: BorderPane? = null

    @FXML
    var layerTreeView: TreeView<String>? = null

    init {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

        image.addListener { o -> canvas.displayImage(image.value.image.toImage()) }

        workflowEngine.finished += {
            println("workflow finished!")
            image.set(it)
        }

        workflowEngine.stepDone += {
            val algorithm = it.first
            val img = it.second

            println("${algorithm.name} finished!")
            image.set(img)
        }
    }

    fun setupView() {
        // init canvas
        canvas.prefWidth(100.0)
        canvas.prefWidth(100.0)

        layoutPane!!.center = canvas

        updateUI()
    }

    fun runWorkflow(e: ActionEvent) {
        workflowEngine.run(defaultWorkflow, image.value, true, true)
    }

    fun nextStep(e: ActionEvent) {
        workflowEngine.nextStep()

        //todo: bake & set image
    }

    fun updateLayers() {
        val rootItem = CheckBoxTreeItem("layers")
        rootItem.isExpanded = true

        layerTreeView!!.isShowRoot = false
        layerTreeView!!.isEditable = true
        layerTreeView!!.cellFactory = CheckBoxTreeCell.forTreeView()

        for (layer in canvas.layers.reversed()) {
            var layerItem = CheckBoxTreeItem("${layer.name} (${layer.shapes.size} Items)")
            layerItem.isSelected = layer.visible
            layerItem.selectedProperty().addListener { o ->
                run {
                    layer.visible = layerItem.isSelected
                    canvas.redraw()
                }
            }
            rootItem.children.add(layerItem)

            for (shape in layer.shapes) {
                var shapeItem = CheckBoxTreeItem("$shape")
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
            "rulerButton" -> canvas.activeTool = ViewTool()
            "lineButton" -> canvas.activeTool = LineTool()
            "rectButton" -> canvas.activeTool = RectangleTool()
            "eraseButton" -> canvas.activeTool = RectangleTool()
        }
    }
}