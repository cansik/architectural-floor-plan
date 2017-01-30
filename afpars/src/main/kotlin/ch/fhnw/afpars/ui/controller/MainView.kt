package ch.fhnw.afpars.ui.controller

import ch.fhnw.afpars.io.reader.AFImageReader
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.ui.control.editor.ImageEditor
import ch.fhnw.afpars.ui.control.editor.Layer
import ch.fhnw.afpars.ui.control.editor.shapes.RectangleShape
import ch.fhnw.afpars.util.toImage
import ch.fhnw.afpars.util.toMat
import javafx.beans.property.SimpleObjectProperty
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.geometry.Dimension2D
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.input.Clipboard
import javafx.scene.layout.BorderPane
import javafx.scene.paint.ImagePattern
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

    @FXML
    var layoutPane: BorderPane? = null

    init {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

        image.addListener { o -> displayImage(image.value.image.toImage()) }
    }

    fun setupView() {
        // init canvas
        canvas.prefWidth(100.0)
        canvas.prefWidth(100.0)

        layoutPane!!.center = canvas
    }

    fun displayImage(image: Image) {
        canvas.resizeCanvas(image.width, image.height)

        // set layer
        val imageLayer = Layer("Image")
        val drawLayer = Layer("Draw")

        val imageRect = RectangleShape()
        imageRect.size = Dimension2D(image.width, image.height)
        imageRect.noStroke()
        imageRect.fill = ImagePattern(image, 0.0, 0.0, image.width, image.height, false)

        imageLayer.shapes.add(imageRect)

        canvas.layers.clear()
        canvas.layers.add(imageLayer)
        canvas.layers.add(drawLayer)
        canvas.activeLayer = drawLayer
        canvas.redraw()
    }

    fun loadImageFromClipBoard(e: ActionEvent) {
        val cb = Clipboard.getSystemClipboard()
        if (cb.hasImage()) {
            image.set(AFImage(cb.image.toMat()))
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
        }
    }
}