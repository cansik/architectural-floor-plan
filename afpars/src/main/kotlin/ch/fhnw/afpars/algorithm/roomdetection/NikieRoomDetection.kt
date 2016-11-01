package ch.fhnw.afpars.algorithm.roomdetection

import ch.fhnw.afpars.algorithm.AlgorithmParameter
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.util.copy
import ch.fhnw.afpars.util.geodesicDilate
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
        var distTransform = img.image.zeros()
        val distMinus = img.image.zeros()
        val geodesicDistTransform = img.image.zeros()
        var markers = img.image.zeros()
        var markersBefore = img.image.zeros()
        val invDistTransform = img.image.zeros()

        // distance transform
        Imgproc.cvtColor(img.image, img.image, Imgproc.COLOR_BGR2GRAY)
        img.image.convertTo(img.image, CvType.CV_8UC1)
        Imgproc.distanceTransform(img.image, distTransform, Imgproc.CV_DIST_L2, Imgproc.CV_DIST_MASK_PRECISE)

        // find center of distance transform points
        // todo: maybe better approach: Regional maxima of opening-closing by reconstruction (fgm)
        Core.subtract(distTransform, Scalar(differenceScalar), distMinus)
        distMinus.geodesicDilate(distTransform, geodesicDilateSize, geodesicDistTransform)
        Core.bitwise_xor(geodesicDistTransform, distTransform, markers)

        /*
        markers = Imgcodecs.imread("data/watershed/marker.png")
        Imgproc.cvtColor(markers, markers, Imgproc.COLOR_BGR2GRAY)
        markers.convertTo(markers, CvType.CV_8UC1)

        distTransform = Imgcodecs.imread("data/watershed/transform.png")
        Imgproc.cvtColor(distTransform, distTransform, Imgproc.COLOR_BGR2GRAY)
        distTransform.convertTo(distTransform, CvType.CV_8UC1)

        // convert image formats for watershed
        distTransform.negate(invDistTransform)
        Imgproc.cvtColor(invDistTransform, invDistTransform, Imgproc.COLOR_GRAY2BGR)
        invDistTransform.convertTo(invDistTransform, CvType.CV_8UC3)
        */

        //markers.copyTo(markersBefore)
        markersBefore = markers.copy()
        markers.convertTo(markers, CvType.CV_32S)

        // watershed with marker
        Imgproc.watershed(invDistTransform, markers)
        markers.convertTo(markers, CvType.CV_8U)

        // add history
        history.add(AFImage(image.image, "Input"))
        history.add(AFImage(distTransform, "DistTransform"))
        history.add(AFImage(distMinus, "Dist Minus"))
        history.add(AFImage(geodesicDistTransform, "Geodesic"))
        history.add(AFImage(markersBefore, "Markers before"))
        history.add(AFImage(invDistTransform, "Inverse DistT"))
        history.add(AFImage(markers, "Markers"))

        return AFImage(markers)
    }
}