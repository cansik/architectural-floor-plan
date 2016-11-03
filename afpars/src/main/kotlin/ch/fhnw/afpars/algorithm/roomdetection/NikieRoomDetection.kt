package ch.fhnw.afpars.algorithm.roomdetection

import ch.fhnw.afpars.algorithm.AlgorithmParameter
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.util.*
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.MatOfPoint
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


    /*
    Input ist ein morphologisch transformiertes Bild
    Eingabetyp ist 32SC1
     */
    override fun run(image: AFImage, history: MutableList<AFImage>): AFImage {
        //Originalbild
        val original = image.clone()
        //Distanztransformatin
        val distTransform = original.image.zeros()
        //GeodesicTransform
        val geodesicTransform = original.image.zeros()
        //Markers
        val markers = original.image.zeros()
        //Foreground
        val foreground = original.image.zeros()


        /*
        Distanztranformation
        Geht vom Originalbild aus, rückgabe ist das Bild "distTransform"
        Konvertiert das Bild zu 8UC1
         */
        var localoriginal = original.image.zeros()
        Imgproc.cvtColor(original.image, localoriginal, Imgproc.COLOR_BGR2GRAY)
        Imgproc.distanceTransform(localoriginal, distTransform, Imgproc.CV_DIST_L2, Imgproc.CV_DIST_MASK_PRECISE)

        //Background
        val background = localoriginal.copy()
        //Summed grounds
        val summedUp = localoriginal.zeros()


        /*
        GeodesicDilation
         */
        val darkerDistTransform = original.image.zeros()
        Core.subtract(distTransform, Scalar(differenceScalar), darkerDistTransform)
        darkerDistTransform.geodesicDilate(distTransform, geodesicDilateSize, geodesicTransform)
        Core.compare(distTransform, geodesicTransform, markers, Core.CMP_LE)


        /*
        Invert markers
         */
        Imgproc.threshold(markers, foreground, 128.0, 255.0, Imgproc.THRESH_BINARY_INV)

        /*
        findContours
         */
        val contours = mutableListOf<MatOfPoint>()
        val hierarchy = original.image.zeros()
        Imgproc.findContours(foreground.copy(), contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE)

        // Create the marker image for the watershed algorithm
        val contmarkers = image.image.zeros(CvType.CV_32SC1)
        contmarkers.setTo(Scalar(255.0))

        var i = 0

        //Draw foreground markers
        for (cnt in contours) {
            Imgproc.drawContours(contmarkers, mutableListOf(cnt), 0, Scalar.all((i + 200).toDouble()), Core.FILLED)
            i++;
        }

        for (i in 0..background.height() - 1) {
            for (j in 0..contmarkers.width() - 1) {
                if (contmarkers.get(i, j)[0].equals(255.0)) {
                    contmarkers.put(i, j, 0.0)
                }
            }
        }

        /*
        Background
         */
        for (i in 0..background.height() - 1) {
            for (j in 0..background.width() - 1) {
                if (background.get(i, j)[0].equals(0.0)) {
                    background.put(i, j, 128.0)
                } else if (background.get(i, j)[0].equals(255.0)) {
                    background.put(i, j, 0.0)
                }
            }
        }

        /*
        Kombination Background/Foreground
         */
        for (i in 0..summedUp.height() - 1) {
            for (j in 0..summedUp.width() - 1) {
                summedUp.put(i, j, contmarkers.get(i, j)[0] + background.get(i, j)[0])
            }
        }

        var watershedoriginal = localoriginal.copy()
        Imgproc.cvtColor(localoriginal, watershedoriginal, Imgproc.COLOR_GRAY2BGR)
        watershedoriginal = watershedoriginal.to8UC3()
        val watershed = summedUp.to32S()

        Imgproc.watershed(watershedoriginal, watershed)

/*
        //val img = image.clone()
        //var distTransform = img.image.zeros()
        val distMinus = original.image.zeros()
        val geodesicDistTransform = original.image.zeros()
        var markers = original.image.zeros()
        var markersBefore = original.image.zeros()
        val invDistTransform = original.image.zeros()
        val bindist = original.image.zeros()

        // distance transform
        Imgproc.cvtColor(original.image, original.image, Imgproc.COLOR_BGR2GRAY)
        original.image.convertTo(original.image, CvType.CV_8UC1)
        Imgproc.distanceTransform(original.image, distTransform, Imgproc.CV_DIST_L2, Imgproc.CV_DIST_MASK_PRECISE)


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
        val hierarchy = original.image.zeros()
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
        val uc8markers1 = markers1.to8UC1();
        //Invert
        Imgproc.threshold(uc8markers1, uc8markers1, treshold, 255.0, Imgproc.THRESH_BINARY_INV)


        val processedmarkers = uc8markers1.to32SC1()
        for (i in 0..processedmarkers.height() - 1) {
            for (j in 0..processedmarkers.width() - 1) {
                if (processedmarkers.get(i, j).get(0).equals(0.0)) {
                    processedmarkers.put(i, j, 1.0)
                }
            }
        }

        //watershed
        Imgproc.watershed(image.image, processedmarkers)
        for (i in 0..processedmarkers.height() - 1) {
            var print = ""
            for (j in 0..processedmarkers.width() - 1) {
                print += processedmarkers.get(i, j).get(0).toString() + " "
            }
            System.out.println(print)
        }
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
        history.add(AFImage(uc8markers1, "uc8Markers1"))
        history.add(AFImage(processedmarkers, "processedMarkers1"))

        history.add(AFImage(distMinus, "Dist Minus"))
        history.add(AFImage(geodesicDistTransform, "Geodesic"))
        history.add(AFImage(markersBefore, "Markers before"))
        history.add(AFImage(invDistTransform, "Inverse DistT"))
        history.add(AFImage(markers, "Markers"))
*/
        history.add(AFImage(distTransform, "Distanztransformation"))
        history.add(AFImage(geodesicTransform, "Geodesictransformation"))
        history.add(AFImage(markers, "Markers"))
        history.add(AFImage(foreground, "Foreground"))
        history.add(AFImage(contmarkers, "findContour"))
        history.add(AFImage(background, "Background"))
        history.add(AFImage(summedUp, "Summed Up"))
        history.add(AFImage(watershed, "Watershed"))
        return AFImage(markers)
    }
}