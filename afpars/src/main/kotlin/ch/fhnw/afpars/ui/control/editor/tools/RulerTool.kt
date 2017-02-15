package ch.fhnw.afpars.ui.control.editor.tools

import ch.fhnw.afpars.ui.control.editor.ImageEditor
import ch.fhnw.afpars.util.format
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.TextInputDialog
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop


/**
 * Created by cansik on 03.02.17.
 */
class RulerTool : LineTool() {
    /**
     * Pixel length in centimeters.
     */
    val pixelLength = SimpleObjectProperty<Double>(-1.0)

    val isMeasured: Boolean
        get() = pixelLength.value > 0

    init {
        defaultStrokeWeight = 5.0
        defaultStroke = LinearGradient(0.0, 0.0, 1.0, 0.0, true, CycleMethod.NO_CYCLE, Stop(0.0, Color.ORANGE), Stop(1.0, Color.RED))
    }

    override fun onCanvasMouseReleased(imageEditor: ImageEditor, event: MouseEvent) {
        showInputDialog()
        imageEditor.activeLayer.shapes.remove(current)
        super.onCanvasMouseReleased(imageEditor, event)
    }

    private fun showInputDialog() {
        val distance = current.point1.distance(current.point2)

        val dialog = TextInputDialog(distance.format(2))
        dialog.title = "Ruler Dialog"
        dialog.headerText = "Set the relation of a pixel to cm."
        dialog.contentText = "Set how long are ${distance.format(2)} px in cm:"

        val result = dialog.showAndWait()
        result.ifPresent({ lengthText ->

            try {
                val value = lengthText.toDouble()

                println("Distance: $distance")
                println("Value: $value")

                pixelLength.set(value / distance)
                println("One Pixel is: ${pixelLength.value} cm")
            } catch (ex: Exception) {
                println("Argument is not a double!")
            }
        })
    }
}