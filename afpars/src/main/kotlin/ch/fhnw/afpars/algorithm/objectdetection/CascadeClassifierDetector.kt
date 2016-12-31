package ch.fhnw.afpars.algorithm.objectdetection

import ch.fhnw.afpars.algorithm.AlgorithmParameter
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.util.copy
import ch.fhnw.afpars.util.zeros
import org.opencv.core.MatOfRect
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier


/**
 * Created by cansik on 22.12.16.
 */
class CascadeClassifierDetector : IObjectDetectionAlgorithm {
    companion object {
        val CASCADE_ATTRIBUT = "cascadeareas"
    }

    override val name: String
        get() = "Cascade Classifier Object Detector"

    @AlgorithmParameter(name = "Erosion Size")
    val erosionSize: Double = 10.0

    override fun run(image: AFImage, history: MutableList<AFImage>): AFImage {
        val faceDetector = CascadeClassifier("cascade-files/cascade_thicken.xml")

        // morphological transform
        val element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
                Size(erosionSize, erosionSize))

        val preparedImage = image.image.zeros()
        Imgproc.erode(image.image, preparedImage, element)

        // Detect objects in the image.
        // MatOfRect is a special container class for Rect.
        val areas = MatOfRect()
        val result = image.image.copy()

        faceDetector.detectMultiScale(preparedImage, areas)

        println("Detected ${areas.toArray().size} objects")

        // Draw a bounding box around each face.
        for (rect in areas.toArray()) {
            Imgproc.rectangle(result, Point(rect.x.toDouble(), rect.y.toDouble()),
                    Point(rect.x + rect.width.toDouble(), rect.y + rect.height.toDouble()), Scalar(0.0, 255.0, 0.0))
        }

        history.add(AFImage(preparedImage, "Erode Image"))
        history.add(AFImage(result, "Marked Image"))

        image.attributes.put(CASCADE_ATTRIBUT, areas)
        return image
    }
}