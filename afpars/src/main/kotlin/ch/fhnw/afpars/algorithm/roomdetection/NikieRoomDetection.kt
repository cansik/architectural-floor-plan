package ch.fhnw.afpars.algorithm.roomdetection

import ch.fhnw.afpars.algorithm.AlgorithmParameter
import ch.fhnw.afpars.algorithm.preprocessing.MorphologicalTransform
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.util.copy
import ch.fhnw.afpars.util.geodesicDilate
import ch.fhnw.afpars.util.zeros
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

/**
 * Based on http://mathematica.stackexchange.com/a/19550/43125 by nikie
 */
class NikieRoomDetection : IRoomDetectionAlgorithm {
    override val name: String
        get() = "Nikie Room Detection"

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
        val distTransform = img.image.copy()
        val mask = img.image.copy()
        var markers = img.image.copy()

        Core.subtract(markers, Scalar(differenceScalar), markers)

        markers = markers.geodesicDilate(mask, geodesicDilateSize)
        Core.absdiff(markers, distTransform, markers)

        // just for preview
        MorphologicalTransform().threshold(markers, treshold)

        // invert dist transform
        val invertedColorMatrix = distTransform.zeros().setTo(Scalar(255.0))
        Core.subtract(invertedColorMatrix, distTransform, distTransform)

        // watershed

        //todo: Check if it is opencv3.1 bug: http://answers.opencv.org/question/64815/opencv-error-assertion-failed-in-watershed/

        //The first should be an 8-bit, 3-channel image, and the second should be a 32-bit single-channel image.
        val mTrans = Mat(distTransform.rows(), distTransform.cols(), CvType.CV_8UC3)
        distTransform.convertTo(mTrans, CvType.CV_8UC3)

        val mDist32 = Mat(markers.rows(), markers.cols(), CvType.CV_32SC1) // 32 bit signed 1 channel, use CV_32UC1 for unsigned
        markers.convertTo(mDist32, CvType.CV_32SC1, 1.0, 0.0)

        Imgproc.watershed(mTrans, mDist32)

        return AFImage(distTransform)
    }
}