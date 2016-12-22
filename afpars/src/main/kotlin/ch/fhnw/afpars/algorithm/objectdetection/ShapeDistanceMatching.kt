package ch.fhnw.afpars.algorithm.objectdetection

import ch.fhnw.afpars.model.AFImage
import org.opencv.core.Mat
import org.opencv.core.MatOfRect
import org.opencv.core.Rect

/**
 * Created by cansik on 22.12.16.
 */
class ShapeDistanceMatching : IObjectDetectionAlgorithm {
    override val name: String
        get() = "Shape Distance Matching"

    override fun run(image: AFImage, history: MutableList<AFImage>): AFImage {
        val areas = MatOfRect(image.attributes[CascadeClassifierDetector.CASCADE_ATTRIBUT])

        for (rect in areas.toArray().sortedByDescending(Rect::area).take(100)) {
            val part = Mat(image.image, rect)
            history.add(AFImage(part, "Area: ${rect.area()}"))
        }

        return image
    }
}