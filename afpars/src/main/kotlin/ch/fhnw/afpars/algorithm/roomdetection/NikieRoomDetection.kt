package ch.fhnw.afpars.algorithm.roomdetection

import ch.fhnw.afpars.algorithm.AlgorithmParameter
import ch.fhnw.afpars.algorithm.preprocessing.MorphologicalTransform
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.util.geodesicDilate
import ch.fhnw.afpars.util.negate
import ch.fhnw.afpars.util.zeros
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

/**
 * Based on http://mathematica.stackexchange.com/a/19550/43125 by nikie
 */
class NikieRoomDetection : IRoomDetectionAlgorithm {
    override val name: String
        get() = "Nikie Room Detection"

    @AlgorithmParameter(name = "Difference Scalar")
    var differenceScalar = 70.0

    @AlgorithmParameter(name = "Geodesic Dilate")
    var geodesicDilateSize = 88

    @AlgorithmParameter(name = "Threshold", minValue = 0.0, maxValue = 255.0)
    var treshold = 26.0

    override fun run(image: AFImage, history: MutableList<AFImage>): AFImage {
        val img = image.clone()
        val distTransform = img.image.zeros()
        val geodesicDistTransform = img.image.zeros()
        val markers = img.image.zeros()
        val markersBefore = img.image.zeros()
        val invDistTransform = img.image.zeros()

        // distance transform
        Imgproc.cvtColor(img.image, img.image, Imgproc.COLOR_BGR2GRAY)
        img.image.convertTo(img.image, CvType.CV_8UC1)
        Imgproc.distanceTransform(img.image, distTransform, Imgproc.CV_DIST_L2, Imgproc.CV_DIST_MASK_PRECISE)

        // find center of distance transform points
        Core.subtract(distTransform, Scalar(differenceScalar), geodesicDistTransform)
        geodesicDistTransform.geodesicDilate(distTransform, geodesicDilateSize)
        Core.subtract(distTransform, geodesicDistTransform, markers)
        MorphologicalTransform().threshold(markers, treshold)

        // convert image formats for watershed
        distTransform.negate(invDistTransform)

        Imgproc.cvtColor(invDistTransform, invDistTransform, Imgproc.COLOR_GRAY2BGR)
        invDistTransform.convertTo(invDistTransform, CvType.CV_8UC3)

        markers.copyTo(markersBefore)
        markers.convertTo(markers, CvType.CV_32S)

        // watershed with marker
        Imgproc.watershed(invDistTransform, markers)
        markers.convertTo(markers, CvType.CV_8U)

        // add history
        history.add(AFImage(image.image, "Input"))
        history.add(AFImage(distTransform, "DistTransform"))
        history.add(AFImage(geodesicDistTransform, "Geodesic"))
        history.add(AFImage(markersBefore, "Markers before"))
        history.add(AFImage(invDistTransform, "Inverse DistT"))
        history.add(AFImage(markers, "Markers"))

        return AFImage(img.image)
    }
}