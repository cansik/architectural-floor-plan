package ch.fhnw.afpars.algorithm.roomdetection

import ch.fhnw.afpars.model.AFImage
import org.opencv.imgproc.Imgproc

/**
 * Based on http://mathematica.stackexchange.com/a/19550/43125 by nikie
 */
class NikieRoomDetection : IRoomDetectionAlgorithm {

    override fun run(image: AFImage): AFImage {
        val img = image.clone()

        Imgproc.cvtColor(img.image, img.image, Imgproc.COLOR_BGR2GRAY);
        Imgproc.distanceTransform(img.image, img.image, Imgproc.CV_DIST_L2, Imgproc.CV_DIST_MASK_PRECISE)

        return img
    }
}