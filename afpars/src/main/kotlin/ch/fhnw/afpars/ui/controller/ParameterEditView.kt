package ch.fhnw.afpars.ui.controller

import ch.fhnw.afpars.algorithm.AlgorithmParameter
import ch.fhnw.afpars.algorithm.IAlgorithm
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.ui.control.RelationNumberField
import ch.fhnw.afpars.ui.control.editor.ImageEditor
import ch.fhnw.afpars.util.format
import ch.fhnw.afpars.util.toImage
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.embed.swing.SwingFXUtils
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import javafx.stage.Stage
import javafx.util.Callback
import java.io.IOException
import java.lang.reflect.Field
import javax.imageio.ImageIO
import kotlin.properties.Delegates

/**
 * Created by Alexander on 12.10.2016.
 */
class ParameterEditView {
    @FXML
    lateinit var previewImage: ImageEditor

    @FXML
    lateinit var editControlBox: VBox

    @FXML
    lateinit var historyListView: ListView<AFImage>

    var historyImages = FXCollections.observableArrayList<AFImage>()

    var image: AFImage by Delegates.notNull()

    var algorithm: IAlgorithm by Delegates.notNull()

    var fields: List<Field> by Delegates.notNull()

    var lastSelectedImage = 0

    var result: AFImage by Delegates.notNull()

    var isOK : Boolean = false

    var inputFields = mutableListOf<RelationNumberField>()

    var currentImage : AFImage? = null

    fun initView(algorithm: IAlgorithm, image: AFImage) {
        this.image = image
        this.algorithm = algorithm

        // set image size
        //this.image = this.image.resize(1000, 0)

        fields = getAlgorithmParameters(algorithm)

        // create ui elements for fields
        fields.forEach { createFieldElement(it) }

        historyListView.items = historyImages
        historyListView.cellFactory = Callback { listView ->
            object : ListCell<AFImage>() {
                override fun updateItem(item: AFImage?, empty: Boolean) {
                    if (item != null) {
                        super.updateItem(item, empty)

                        if (empty) {
                            graphic = null
                        } else {
                            // true makes this load in background
                            val imageView = ImageView(item.image.toImage())
                            imageView.preserveRatioProperty().set(true)
                            imageView.fitWidth = historyListView.width * 0.75

                            val label = Label(item.name)
                            graphic = VBox(label, imageView)
                        }
                    }
                }
            }
        }
        historyListView.selectionModel.selectedItemProperty().addListener { observable, oldValue, newValue ->
            if (newValue != null) {
                previewImage.resetZoom()
                previewImage.displayImage(newValue.image.toImage())

                currentImage = newValue
            }
        }

        runAlgorithm()
    }

    fun nextButtonClicked() {
        isOK = true
        val stage = previewImage.scene.window as Stage
        stage.close()
    }

    fun cancelButtonClicked() {
        isOK = false
        val stage = previewImage.scene.window as Stage
        stage.close()
    }

    fun saveButtonClicked(e : ActionEvent)
    {
        if(currentImage == null)
            return

        val stage = (e.source as Node).scene.window as Stage

        val fileChooser = FileChooser()
        fileChooser.initialFileName = "${currentImage!!.name}.png"
        fileChooser.title = "Save image as png"
        fileChooser.extensionFilters.addAll(FileChooser.ExtensionFilter("Portable Network Graphics", "*.png"))

        val file = fileChooser.showSaveDialog(stage)

        if (file != null) {
            val outputImage = currentImage!!.image.toImage()

            try {
                ImageIO.write(SwingFXUtils.fromFXImage(outputImage, null), "png", file)
            } catch (e: IOException) {
                println("Error: $e")
            }
        }
    }

    private fun runAlgorithm() {
        val history = arrayListOf<AFImage>()
        val result = algorithm.run(image, history)

        // save result
        this.result = result

        Platform.runLater {
            lastSelectedImage = Math.max(0, historyListView.selectionModel.selectedIndex)

            // add history
            historyImages.clear()
            historyImages.addAll(history)
            historyImages.add(result)
            result.name = "Result"

            historyListView.scrollTo(lastSelectedImage)
            historyListView.selectionModel.select(lastSelectedImage)
            historyListView.focusModel.focus(lastSelectedImage)
        }
    }

    private fun createFieldElement(field: Field) {
        val annotation = field.getAnnotation(AlgorithmParameter::class.java)

        // controls
        val nameLabel = Label("${annotation.name}:")
        nameLabel.prefWidth = 100.0

        val valueSlider = Slider()
        valueSlider.prefWidth = 150.0

        val inputField = RelationNumberField(0.0, annotation.minValue, annotation.maxValue)
        inputField.prefWidth = 100.0
        inputFields.add(inputField)

        val tooltip = Tooltip(annotation.name
                + System.lineSeparator()
                + annotation.helpText
                + System.lineSeparator()
                + System.lineSeparator()
                + "Min: ${annotation.minValue.format(2)}"
                + System.lineSeparator()
                + "Max: ${annotation.maxValue.format(2)}")

        valueSlider.tooltip = tooltip
        inputField.tooltip = tooltip

        valueSlider.valueProperty().bindBidirectional(inputField.valueProperty())

        valueSlider.min = annotation.minValue
        valueSlider.max = annotation.maxValue
        valueSlider.minorTickCount = 1
        valueSlider.isSnapToTicks = false
        valueSlider.majorTickUnit = annotation.majorTick
        valueSlider.isShowTickLabels = true

        // set slider value
        when (field.type) {
            Int::class.java -> valueSlider.value = (field.get(algorithm) as Int).toDouble()
            Float::class.java -> valueSlider.value = (field.get(algorithm) as Float).toDouble()
            Double::class.java -> valueSlider.value = field.get(algorithm) as Double
        }

        valueSlider.valueProperty().addListener { observableValue, old, new ->
            run {
                when (field.type) {
                    Int::class.java -> field.set(algorithm, new.toInt())
                    Float::class.java -> field.set(algorithm, new.toFloat())
                    Double::class.java -> field.set(algorithm, new.toDouble())
                }

                // run algorithm on change
                runAlgorithm()
            }
        }

        // show controls
        val box = HBox(nameLabel, valueSlider, inputField)
        box.spacing = 10.0
        editControlBox.children.add(box)
    }

    private fun getAlgorithmParameters(obj: IAlgorithm): List<Field> {
        val c = obj.javaClass

        val fields = c.declaredFields.filter { it.isAnnotationPresent(AlgorithmParameter::class.java) }
        fields.forEach { it.isAccessible = true }
        return fields
    }

    fun setupView() {
        previewImage.redraw()
        previewImage.resetZoom()
        previewImage.resize()

        // fixes lazy resize of input fields
        inputFields.forEach {
            it.resize()
            it.resizeAnimation()
        }
    }

}