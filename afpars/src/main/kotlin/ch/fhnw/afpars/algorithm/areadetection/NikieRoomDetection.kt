package ch.fhnw.afpars.algorithm.areadetection

import ch.fhnw.afpars.algorithm.AlgorithmParameter
import ch.fhnw.afpars.algorithm.objectdetection.CascadeClassifierDetector
import ch.fhnw.afpars.io.reader.AFImageReader
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.util.*
import ch.fhnw.afpars.util.opencv.combinePoints
import ch.fhnw.afpars.util.opencv.sparsePoints
import javafx.stage.FileChooser
import org.bytedeco.javacpp.opencv_core
import org.bytedeco.javacpp.opencv_features2d
import org.bytedeco.javacpp.opencv_objdetect
import org.opencv.core.*
import org.opencv.features2d.DescriptorExtractor
import org.opencv.features2d.DescriptorMatcher
import org.opencv.features2d.FeatureDetector
import org.opencv.features2d.Features2d
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import java.beans.FeatureDescriptor
import java.io.File
import java.nio.file.Path


/**
 * Based on http://mathematica.stackexchange.com/a/19550/43125 by nikie
 */
class NikieRoomDetection : IAreaDetectionAlgorithm {
    override val name: String
        get() = "Nikie Room Detection"

    @AlgorithmParameter(name = "Difference Scalar")
    var differenceScalar = 70.0

    @AlgorithmParameter(name = "Geodesic Dilate")
    var geodesicDilateSize = 88

    @AlgorithmParameter(name = "Distance", minValue = 1.0, maxValue = 10.0)
    var distance1 = 4

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
        //Keypoints
        var drawkeypoints = original.clone().image

        val file = File("C:\\Users\\AlexL\\OneDrive\\Bilder\\test.jpg")
        val door = AFImageReader().read(file.toPath())
        var drawkeypoints1 = door.clone().image

        val file1 = File("D:\\FHNW\\Bachelor\\architectural-floor-plan\\afpars\\data\\A_N1.png")
        val basic = AFImageReader().read(file1.toPath())

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
       Cornerdetection
        */
        var cornerdet = localoriginal.copy()
        val cornerdetnorm = cornerdet.zeros()
        val cornerdetnormscaled = cornerdet.zeros()
        //cornerdet = cornerdet.to32SC1()
        Imgproc.cornerHarris(cornerdet, cornerdet, 3, 5, 0.04)
        Core.normalize(cornerdet, cornerdetnorm, 0.0, 255.0, Core.NORM_MINMAX, CvType.CV_32FC1, Mat());
        Core.convertScaleAbs(cornerdetnorm, cornerdetnormscaled);
        val threshlow = 100
        val threshhigh = 170
        val points = mutableListOf<Point>()
        // Drawing a circle around corners
        for (j in 0..cornerdetnorm.rows() - 1) {
            var text = ""
            for (i in 0..cornerdetnorm.cols() - 1) {
                text += cornerdetnormscaled.get(j, i)[0].toInt().toString() + " "
                val point = cornerdetnorm.get(j, i)[0]
                //if (point > threshlow) {
                if (point > threshhigh) {
                    //Imgproc.circle(cornerdetnormscaled, Point(i.toDouble(), j.toDouble()), 10, Scalar(0.0), 2, 8, 0);
                    points.add(Point(i.toDouble(), j.toDouble()))
                }
                //}
            }
            //System.out.println(text + "Line: " + j + "; ")
        }

        val sparsePoints = points.sparsePoints(distance1.toDouble()).combinePoints()
        for (p in sparsePoints)
            Imgproc.circle(cornerdetnormscaled, p, 8, Scalar(0.0, 0.0, 255.0))
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
            Imgproc.drawContours(contmarkers, mutableListOf(cnt), 0, Scalar.all((/*i +*/ 200).toDouble()), Core.FILLED)
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



        //val keypoints1 = MatOfKeyPoint()
        //val keypoints2 = MatOfKeyPoint()
        /*        val keypoints1 = opencv_core.KeyPointVector()
        val keypoints2 = opencv_core.KeyPointVector()
        val descriptors1 = opencv_core.Mat()
        val descriptors2 = opencv_core.Mat()*/
        //val detector = FeatureDetector.create(FeatureDetector.ORB)

        /*val fileyml= File("D:\\FHNW\\Semester7\\architectural-floor-plan\\afpars\\src\\main\\resources\\parameters\\orb.yml")
        detector.read(fileyml.absolutePath)
        val extractor = DescriptorExtractor.create(DescriptorExtractor.ORB)
*/
/*        val orb = opencv_features2d.ORB.create(10000, 1.2f, 8, 31, 0, 2, 0, 31, 15)
        val basicjcv = basic.image.convertToJavaCV()
        orb.detect(basicjcv, keypoints1)
        orb.compute(basicjcv, keypoints1, descriptors1)
        Features2d.drawKeypoints(basic.image, keypoints1.convertToOpenCV(), drawkeypoints, Scalar(255.0, 0.0, 0.0), 0)

        val doorjcv = door.image.convertToJavaCV()
        val orb1 = opencv_features2d.ORB.create(10, 1.2f, 8, 0, 0, 2, 0, 31, 15)
        orb1.detect(doorjcv, keypoints2)
        orb1.compute(doorjcv, keypoints2, descriptors2)
        Features2d.drawKeypoints(door.image, keypoints2.convertToOpenCV(), drawkeypoints1, Scalar(255.0, 0.0, 0.0), 0)*/
/*
        val keypoints3 = MatOfKeyPoint()
        val keypoints4 = MatOfKeyPoint()

        val descriptors3 = Mat()
        val descriptors4 = Mat()

        val detector = FeatureDetector.create(FeatureDetector.ORB)
        val fileyml= File("D:\\FHNW\\Semester7\\architectural-floor-plan\\afpars\\src\\main\\resources\\parameters\\orb.yml")
        detector.read(fileyml.absolutePath)
        val extractor = DescriptorExtractor.create(DescriptorExtractor.ORB)

        basic.image = basic.image.to8UC3()
        door.image = door.image.to8UC3()
        detector.detect(basic.image, keypoints3)
        detector.detect(door.image, keypoints4)

        extractor.compute(basic.image, keypoints3, descriptors3)
        extractor.compute(door.image, keypoints4, descriptors4)

        Features2d.drawKeypoints(basic.image,keypoints3,drawkeypoints, Scalar(255.0,0.0,0.0),0)
        val matches = MatOfDMatch()
        val matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING)

        matcher.match(descriptors3, descriptors4 ,matches)*/
/*        val result = opencv_core.Mat()
        val matchvec = opencv_core.DMatchVector()
        opencv_features2d.BFMatcher().match(descriptors1, descriptors2, matchvec, result)

        //Traincascade
        val cascade = CascadeClassifierDetector()
        val options = MatOfRect()
//        cascade.detectMultiScale(basic.image, options)

*/
        val haaralg = CascadeClassifierDetector()
        haaralg.erosionSize = 2.0
        haaralg.minNeighbors = 3
        haaralg.scaleFactor = 1.1
        val res = haaralg.run(AFImage(image.attributes.get(AFImageReader.ORIGINAL_IMAGE)!!.copy(), "Haar"), history)
        val foundDoors: MatOfRect = res.attributes.get(CascadeClassifierDetector.CASCADE_ATTRIBUT) as MatOfRect
        val foundDoorsArray = foundDoors.toArray()

        for (i in 0..foundDoors!!.rows() - 1) {
            val door = foundDoorsArray[i]
            val searchDistance = 10
            val doorPoints = mutableListOf<Point>()
            sparsePoints.forEach { point: Point ->
                if (point.x < door.x + door.width + searchDistance && point.x > door.x - searchDistance && point.y < door.y + door.height + searchDistance && point.y > door.y - searchDistance) {
                    doorPoints.add(point)
                    System.out.println("Door found X: " + point.x + " Y: " + point.y + "Iteration: " + i)
                }
            }

            val angles = Array(doorPoints.size) { kotlin.arrayOfNulls<Double>(doorPoints.size) }
            for (j in 0..doorPoints.size - 1) {
                for (k in (j + 1)..doorPoints.size - 1) {
                    angles[j][k] = angleToXAxis(doorPoints[j], doorPoints[k])
                    angles[k][j] = angleToXAxis(doorPoints[j], doorPoints[k])
                }
            }

            //angles.add(angleToXAxis(point1,point2))
            val size = angles[0].size - 1
            for (j in 0..size) {
                for (k in (j + 1)..size) {
                    for (innerJ in 0..size) {
                        for (innerK in (innerJ + 1)..size) {
                            if (innerJ != j && innerK != k && innerJ != k && innerK != j) {
                                if ((angles[j][k] as Double).isApproximate(angles[innerJ][innerK] as Double, 2 * Math.PI / 180)) {
                                    if ((angles[j][innerJ] as Double).isApproximate(angles[k][innerK] as Double, 2 * Math.PI / 180)) {
                                        System.out.println("Door rly found j: " + j + " k: " + k + " ij: " + innerJ + " ik: " + innerK)
                                        Imgproc.rectangle(background,doorPoints[j],doorPoints[innerK],Scalar(128.0,128.0,128.0),-1)
                                    } else if ((angles[j][innerK] as Double).isApproximate(angles[k][innerJ] as Double, 2 * Math.PI / 180)) {
                                        System.out.println("Door rly found j: " + j + " k: " + k + " ij: " + innerJ + " ik: " + innerK)
                                        Imgproc.rectangle(background,doorPoints[j],doorPoints[innerK],Scalar(128.0,128.0,128.0),-1)
                                    }
                                }
                            }
                        }
                    }

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

        history.add(AFImage(cornerdet, "Cornerdet"))
        history.add(AFImage(cornerdetnormscaled, "CornerdetScaled"))
        history.add(AFImage(distTransform, "Distanztransformation"))
        history.add(AFImage(geodesicTransform, "Geodesictransformation"))
        history.add(AFImage(markers, "Markers"))
        history.add(AFImage(foreground, "Foreground"))
        history.add(AFImage(contmarkers, "findContour"))
        history.add(AFImage(background, "Background"))
        history.add(AFImage(summedUp, "Summed Up"))
        history.add(AFImage(watershed, "Watershed"))
        history.add(AFImage(drawkeypoints, "Keypoints"))
        history.add(AFImage(drawkeypoints1, "Keypoints"))
        history.add(res)
        return AFImage(watershed)
    }

    fun angleToXAxis(point1: Point, point2: Point): Double {
        val delta = Point(point1.x - point2.x, point1.y - point2.y)
        return -Math.atan(delta.y / delta.x)
    }
}