package ch.fhnw.afpars.algorithm.objectdetection

import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.util.copy
import org.opencv.core.MatOfRect
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier


/**
 * Created by cansik on 22.12.16.
 */
class CascadeClassifierDetector : IObjectDetectionAlgorithm {
    override val name: String
        get() = "Cascade Classifier Object Detector"

    override fun run(image: AFImage, history: MutableList<AFImage>): AFImage {
        val faceDetector = CascadeClassifier("cascade-files/cascade_thicken.xml")

        // Detect objects in the image.
        // MatOfRect is a special container class for Rect.
        val faceDetections = MatOfRect()
        val result = image.image.copy()

        faceDetector.detectMultiScale(image.image, faceDetections)

        println("Detected ${faceDetections.toArray().size} objects")

        // Draw a bounding box around each face.
        for (rect in faceDetections.toArray()) {
            Imgproc.rectangle(result, Point(rect.x.toDouble(), rect.y.toDouble()),
                    Point(rect.x + rect.width.toDouble(), rect.y + rect.height.toDouble()), Scalar(0.0, 255.0, 0.0))
        }

        history.add(AFImage(result, "Marked Image"))

        return image
    }
}