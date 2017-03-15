package ch.fhnw.afpars.algorithm.structuralanalysis

import ch.fhnw.afpars.algorithm.IAlgorithm
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.util.copy
import ch.fhnw.afpars.util.opencv.SimpleFeatureMatcher
import ch.fhnw.afpars.util.zeros
import org.opencv.core.MatOfRect
import org.opencv.core.Scalar
import org.opencv.features2d.Features2d
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc

/**
 * Created by cansik on 03.01.17.
 */
class OrbDetection : IAlgorithm {
    override val name: String
        get() = "ORB Detection"

    override fun run(image: AFImage, history: MutableList<AFImage>): AFImage {
        val featureMatcher = SimpleFeatureMatcher()

        // load default door and extract features
        val doorTemplate = Imgcodecs.imread("template/door.png")
        val thresholdDoor = doorTemplate.zeros()

        Imgproc.cvtColor(doorTemplate, thresholdDoor, Imgproc.COLOR_BGR2GRAY)
        Imgproc.threshold(thresholdDoor, thresholdDoor, 128.0, 255.0, Imgproc.THRESH_BINARY)

        val doorFeatures = featureMatcher.extract(thresholdDoor)

        // display door features
        val doorWithFeatures = doorTemplate.copy()
        Features2d.drawKeypoints(thresholdDoor, doorFeatures.keypoints, doorWithFeatures, Scalar(255.0, 0.0, 0.0), 0)

        println("Door Keypoints: ${doorFeatures.keypoints.size()}")
        println("Door Descriptors: ${doorFeatures.descriptors.size()}")

        history.add(AFImage(doorTemplate, "Door"))
        history.add(AFImage(doorWithFeatures, "Door Features"))

        // extract features for every part


        return image
    }
}