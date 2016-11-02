package ch.fhnw.afpars.algorithm.roomdetection

import ch.fhnw.afpars.algorithm.AlgorithmParameter
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.util.copy
import ch.fhnw.afpars.util.zeros
import org.opencv.core.*
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
        val bindist = img.image.zeros()

        // distance transform
        Imgproc.cvtColor(img.image, img.image, Imgproc.COLOR_BGR2GRAY)
        img.image.convertTo(img.image, CvType.CV_8UC1)
        Imgproc.distanceTransform(img.image, distTransform, Imgproc.CV_DIST_L2, Imgproc.CV_DIST_MASK_PRECISE)


        //normalize
        //Core.normalize(distTransform, distTransform, 0.0, 1.0, Core.NORM_MINMAX)

        //threshhold
        Imgproc.threshold(distTransform, bindist, treshold, 255.0, Imgproc.THRESH_BINARY)
/*
        //dilation
        val kernel1 = Mat.ones(3, 3, CvType.CV_8UC1);
        Imgproc.dilate(distTransform, distTransform, kernel1);
*/
        //Convert to 8U
        val dist_8u: Mat = image.image.zeros(CvType.CV_8UC1)

        bindist.convertTo(dist_8u, CvType.CV_8UC1)
        history.add(AFImage(dist_8u.copy(), "Distu<8"))
        //Find total markers
        val contours = mutableListOf<MatOfPoint>()
        val hierarchy = img.image.zeros()
        Imgproc.findContours(dist_8u, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE)

        // Create the marker image for the watershed algorithm
        val markers1 = image.image.zeros(CvType.CV_32SC1)
        markers1.setTo(Scalar(255.0))

        var i = 0

        //Draw foreground markers
        for (cnt in contours) {
            if (i > 1) {
                Imgproc.drawContours(markers1, mutableListOf(cnt), 0, Scalar.all((i + 1).toDouble()), Core.FILLED)
            }
            i++;
        }

        //Draw background markers
        Imgproc.circle(markers1, Point(5.0, 5.0), 3, Scalar(255.0, 255.0, 255.0), -1)

        //Invert
        Imgproc.threshold(markers1, markers1, treshold, 255.0, Imgproc.THRESH_BINARY_INV)

        //watershed
        Imgproc.watershed(image.image, markers1)
        //markers1.convertTo(markers1, CvType.CV_8U)
/*
        val mark = Mat.zeros(markers1.size(), CvType.CV_8UC1);
        markers1.convertTo(mark, CvType.CV_8UC1);
        Core.bitwise_not(mark, mark);
        */
        /*
        //    imshow("Markers_v2", mark); // uncomment this if you want to see how the mark
        // image looks like at that point
        // Generate random colors
        val random = Random();
        val upperBound = 256
        val lowerBound = 0
        val colors = Mat.zeros(0, 1, CvType.CV_8UC3)

        for (i in 0..markers1.size().area().toInt()) {
            val b = random.nextInt(upperBound - lowerBound) + lowerBound
            val g = random.nextInt(upperBound - lowerBound) + lowerBound
            val r = random.nextInt(upperBound - lowerBound) + lowerBound
            colors.push_back(Mat(1, 1, CvType.CV_8UC3, Scalar(b.toDouble(), g.toDouble(), r.toDouble())))
            //colors.push_back(Vec3b((uchar) b, (uchar) g, (uchar) r));
        }
        // Create the result image
        var dst = Mat.zeros(markers1.size(), CvType.CV_8UC3);
        // Fill labeled objects with random colors
        for (i in 0..markers1.rows()) {
            for (j in 0..markers1.cols()) {
                val index = markers1.get(i, j)
                if (index > 0 && index <= contours.size().))
                dst.= colors [ index -1];
                else
                dst.put(i, j, Scalar(0.0, 0.0, 0.0))
                dst.at<Vec3b>(i, j) = Scalar(0.0, 0.0, 0.0);
            }
        }
        */

        /*
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
*/
        // add history
        history.add(AFImage(image.image, "Input"))
        history.add(AFImage(distTransform, "DistTransform"))
        history.add(AFImage(bindist, "Markers"))
        history.add(AFImage(dist_8u, "Distu8"))
        history.add(AFImage(markers1, "Markers1"))
        history.add(AFImage(distMinus, "Dist Minus"))
        history.add(AFImage(geodesicDistTransform, "Geodesic"))
        history.add(AFImage(markersBefore, "Markers before"))
        history.add(AFImage(invDistTransform, "Inverse DistT"))
        history.add(AFImage(markers, "Markers"))

        return AFImage(markers1)
    }
}