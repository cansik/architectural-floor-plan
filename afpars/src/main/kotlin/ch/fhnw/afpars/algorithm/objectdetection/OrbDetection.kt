package ch.fhnw.afpars.algorithm.objectdetection

import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.util.copy
import ch.fhnw.afpars.util.opencv.SimpleFeatureMatcher
import org.opencv.core.MatOfRect
import org.opencv.core.Scalar
import org.opencv.features2d.Features2d
import org.opencv.imgcodecs.Imgcodecs

/**
 * Created by cansik on 03.01.17.
 */
class OrbDetection : IObjectDetectionAlgorithm {
    override val name: String
        get() = "ORB Detection"

    override fun run(image: AFImage, history: MutableList<AFImage>): AFImage {
        val areas = MatOfRect(image.attributes[CascadeClassifierDetector.CASCADE_ATTRIBUT])
        val featureMatcher = SimpleFeatureMatcher()

        // load default door and extract features
        val doorTemplate = Imgcodecs.imread("data/door.png")
        val doorFeatures = featureMatcher.extract(doorTemplate)

        // display door features
        val doorWithFeatures = doorTemplate.copy()
        Features2d.drawKeypoints(doorTemplate, doorFeatures.keypoints, doorWithFeatures, Scalar(255.0, 0.0, 0.0), 0)

        history.add(AFImage(doorTemplate, "Door"))
        history.add(AFImage(doorWithFeatures, "Door Features"))

        // extract features for every part


        return image
    }
}