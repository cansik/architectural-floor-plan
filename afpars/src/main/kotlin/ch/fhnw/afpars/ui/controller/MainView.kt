package ch.fhnw.afpars.ui.controller

import ch.fhnw.afpars.io.reader.AFImageReader
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.ui.control.editor.ImageEditor
import javafx.beans.property.SimpleObjectProperty
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
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

    @FXML
    var layoutPane: BorderPane? = null

    init {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
    }

    fun initCanvas(e: ActionEvent) {
        val canvas = ImageEditor()
        canvas.prefWidth(100.0)
        canvas.prefWidth(100.0)

        layoutPane!!.center = canvas
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