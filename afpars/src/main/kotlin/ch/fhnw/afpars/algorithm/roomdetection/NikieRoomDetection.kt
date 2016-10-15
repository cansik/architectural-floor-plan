package ch.fhnw.afpars.algorithm.roomdetection

import ch.fhnw.afpars.algorithm.AlgorithmParameter
import ch.fhnw.afpars.algorithm.preprocessing.MorphologicalTransform
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.util.geodesicDilate
import org.opencv.core.Core
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

/**
 * Based on http://mathematica.stackexchange.com/a/19550/43125 by nikie
 */
class NikieRoomDetection : IRoomDetectionAlgorithm {
    @AlgorithmParameter(name = "Difference Scalar")
    var differenceScalar = 27.0

    @AlgorithmParameter(name = "Geodesic Dilate")
    var geodesicDilateSize = 13

    @AlgorithmParameter(name = "Threshold", minValue = 0.0, maxValue = 255.0)
    var treshold = 26.0

    override fun run(image: AFImage): AFImage {
        val img = image.clone()

        // distance transform
        Imgproc.cvtColor(img.image, img.image, Imgproc.COLOR_BGR2GRAY)
        Imgproc.distanceTransform(img.image, img.image, Imgproc.CV_DIST_L2, Imgproc.CV_DIST_MASK_PRECISE)

        // find center of distance transform
        val mask = img.image.clone()
        var marker = img.image.clone()

        Core.subtract(marker, Scalar(differenceScalar), marker)

        marker = marker.geodesicDilate(mask, geodesicDilateSize)
        Core.absdiff(marker, img.image, img.image)

        // just for preview
        MorphologicalTransform().threshold(img.image, treshold)

        return img
    }
}