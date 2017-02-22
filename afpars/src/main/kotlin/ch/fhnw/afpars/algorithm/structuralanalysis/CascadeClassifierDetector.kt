package ch.fhnw.afpars.algorithm.structuralanalysis

import ch.fhnw.afpars.algorithm.AlgorithmParameter
import ch.fhnw.afpars.algorithm.IAlgorithm
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.ui.control.editor.shapes.RectangleShape
import ch.fhnw.afpars.util.copy
import ch.fhnw.afpars.util.zeros
import javafx.geometry.Dimension2D
import javafx.geometry.Point2D
import javafx.scene.paint.Color
import org.opencv.core.MatOfRect
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier


/**
 * Created by cansik on 22.12.16.
 */
class CascadeClassifierDetector(val cascadeFile:String,val saveName:String) : IAlgorithm {

    override val name: String
        get() = "Cascade Classifier Object Detector"

    @AlgorithmParameter(name = "Erosion Size",minValue = 1.0)
    var erosionSize: Double = 1.0

    @AlgorithmParameter(name = "Scale Factor", minValue = 1.1, maxValue = 5.0, majorTick = 0.1)
    var scaleFactor: Double = 1.1

    @AlgorithmParameter(name = "Min Neighbors", minValue = 1.0, maxValue = 20.0, majorTick = 1.0)
    var minNeighbors: Int = 3


    override fun run(image: AFImage, history: MutableList<AFImage>): AFImage {
        val cascadeDetector = CascadeClassifier(cascadeFile)
        // morphological transform
        val element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
                Size(erosionSize, erosionSize))

        val preparedImage = image.image.zeros()
        Imgproc.erode(image.image, preparedImage, element)

        // Detect objects in the image.
        // MatOfRect is a special container class for Rect.
        val areas = MatOfRect()
        val result = image.image.copy()

        cascadeDetector.detectMultiScale(preparedImage, areas, scaleFactor, minNeighbors, 0, Size(0.0, 0.0), Size(0.0, 0.0))

        println("Detected ${areas.toArray().size} objects")

        // Draw a bounding box around each face.
        for (rect in areas.toArray()) {
            Imgproc.rectangle(result, Point(rect.x.toDouble(), rect.y.toDouble()),
                    Point(rect.x + rect.width.toDouble(), rect.y + rect.height.toDouble()), Scalar(0.0, 255.0, 0.0))
        }

        history.add(AFImage(preparedImage, "Erode Image"))
        history.add(AFImage(result, "Marked Image"))

        image.attributes.put(saveName, areas)
        // add output shapes
        image.addLayer(saveName, *areas.toArray()
                .map { rect ->
                    val s = RectangleShape(
                            location = Point2D(rect.x.toDouble(), rect.y.toDouble()),
                            size = Dimension2D(rect.width.toDouble(), rect.height.toDouble()))
                    s.fill = Color(0.0, 1.0, 0.0, 0.5)
                    s.stroke = Color(0.0, 1.0, 0.0, 1.0)
                    s
                }.toTypedArray())

        return image
    }
}