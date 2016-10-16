package ch.fhnw.afpars.ui.controller

import ch.fhnw.afpars.algorithm.AlgorithmParameter
import ch.fhnw.afpars.algorithm.IAlgorithm
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.ui.control.PreviewImageView
import ch.fhnw.afpars.util.toImage
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.control.Slider
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.util.Callback
import java.lang.reflect.Field
import kotlin.properties.Delegates

/**
 * Created by Alexander on 12.10.2016.
 */
class ParameterEditView {
    @FXML
    var previewImage: PreviewImageView? = null

    @FXML
    var editControlBox: VBox? = null

    @FXML
    var historyListView: ListView<AFImage>? = null

    var historyImages = FXCollections.observableArrayList<AFImage>()

    var image: AFImage by Delegates.notNull()

    var algorithm: IAlgorithm by Delegates.notNull()

    var fields: List<Field> by Delegates.notNull()

    fun initView(algorithm: IAlgorithm, image: AFImage) {
        this.image = image
        this.algorithm = algorithm

        // set image size
        //this.image = this.image.resize(1000, 0)

        fields = getAlgorithmParameters(algorithm)

        // create ui elements for fields
        fields.forEach { createFieldElement(it) }

        historyListView!!.items = historyImages
        historyListView!!.cellFactory = Callback { listView ->
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
                            imageView.fitWidth = historyListView!!.width * 0.75

                            val label = Label(item.name)
                            graphic = VBox(label, imageView)
                        }
                    }
                }
            }
        }
        historyListView!!.selectionModel.selectedItemProperty().addListener { observable, oldValue, newValue ->
            if (newValue != null)
                previewImage!!.newImage(newValue.image.toImage())
        }

        runAlgorithm()
    }

    fun nextButtonClicked() {
        val stage = previewImage!!.scene.window as Stage
        stage.close()
    }

    private fun runAlgorithm() {
        val history = arrayListOf<AFImage>()
        val result = algorithm.run(image, history)

        Platform.runLater {
            // add history
            historyImages.clear()
            historyImages.addAll(history)
            historyImages.add(result)
            result.name = "Result"

            previewImage!!.newImage(result.image.toImage())
        }
    }

    private fun createFieldElement(field: Field) {
        val annotation = field.getAnnotation(AlgorithmParameter::class.java)

        // controls
        val nameLabel = Label(annotation.name)
        val valueSlider = Slider()
        val valueLabel = Label()

        valueSlider.min = annotation.minValue
        valueSlider.max = annotation.maxValue

        // set slider value
        when (field.type) {
            Int::class.java -> valueSlider.value = (field.get(algorithm) as Int).toDouble()
            Float::class.java -> valueSlider.value = (field.get(algorithm) as Float).toDouble()
            Double::class.java -> valueSlider.value = field.get(algorithm) as Double
        }

        valueLabel.text = valueSlider.value.toString()

        valueSlider.valueProperty().addListener { observableValue, old, new ->
            run {
                when (field.type) {
                    Int::class.java -> field.set(algorithm, new.toInt())
                    Float::class.java -> field.set(algorithm, new.toFloat())
                    Double::class.java -> field.set(algorithm, new.toDouble())
                }

                // change value label
                valueLabel.text = valueSlider.value.toString()

                // run algorithm on change
                runAlgorithm()
            }
        }

        // show controls
        editControlBox!!.children.add(HBox(nameLabel, valueSlider, valueLabel))
    }

    private fun getAlgorithmParameters(obj: IAlgorithm): List<Field> {
        val c = obj.javaClass

        val fields = c.declaredFields.filter { it.isAnnotationPresent(AlgorithmParameter::class.java) }
        fields.forEach { it.isAccessible = true }
        return fields
    }

}