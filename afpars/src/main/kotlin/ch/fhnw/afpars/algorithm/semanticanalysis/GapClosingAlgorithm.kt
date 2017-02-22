package ch.fhnw.afpars.algorithm.semanticanalysis

import ch.fhnw.afpars.algorithm.AlgorithmParameter
import ch.fhnw.afpars.algorithm.IAlgorithm
import ch.fhnw.afpars.algorithm.informationsegmentation.MorphologicalTransform
import ch.fhnw.afpars.algorithm.structuralanalysis.CascadeClassifierDetector
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.util.*
import ch.fhnw.afpars.util.opencv.combinePoints
import ch.fhnw.afpars.util.opencv.sparsePoints
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

class GapClosingAlgorithm :IAlgorithm{
    companion object {
        //Colors
        val BLACK = 0.0
        val GRAY = 128.0
        val WHITE = 255.0

        //Angles
        val DOORCLOSINGANGLE = 2 * Math.PI / 180
        val ERRORRECT = 2 * Math.PI / 180

        //Normalize
        val ALPHA = BLACK
        val BETA = WHITE

        //Harris corner
        val BLOCKSIZE = 3
        val KSIZE = 7
        val K = 0.1

        // 5 & 0.12015 254
        // 7 & 0.10101 267

        val CORNERMIN = 170

        //Sparse points
        val RADIUS = 8

        //Door detection
        val ADDDETECTRATIO = 0.25
        val DOORSIZEFACTOR = 1.25
    }

    override val name: String
        get() = "Gap Closing Algorithm"

    @AlgorithmParameter(name = "Distance", minValue = 1.0, maxValue = 10.0)
    var distance1 = 2

    /*
    Input ist ein morphologisch transformiertes Bild
    Eingabetyp ist 32SC1
     */
    override fun run(image: AFImage, history: MutableList<AFImage>): AFImage {
        val watch = Stopwatch()
        watch.start()

        //Originalbild
        val original = image

        val localoriginal = Mat()

        Imgproc.cvtColor(original.image, localoriginal, Imgproc.COLOR_BGR2GRAY)

        /*
       Cornerdetection
        */
        val triple = cornerDetection(localoriginal, watch)
        var cornerdet = triple.first
        val cornerdetnormscaled = triple.second
        val sparsePoints = triple.third


        var watershedoriginal = original.image.copy()
        watershedoriginal = watershedoriginal.to8UC3()

        /*Close doors */
        doorClosing(image, sparsePoints, watch, watershedoriginal)

        history.add(AFImage(cornerdet, "Cornerdet"))
        history.add(AFImage(cornerdetnormscaled, "CornerdetScaled"))
        history.add(AFImage(watershedoriginal, "Orig with door closing"))

        println("${watch.elapsed().toTimeStamp()}\n finished! ${watch.stop().toTimeStamp()}")

        image.image = watershedoriginal
        return image
    }


    private fun doorClosing(image: AFImage, sparsePoints: MutableList<Point>, watch: Stopwatch, watershedoriginal: Mat) {
        println("${watch.elapsed().toTimeStamp()}\nClose doors")
        val foundDoors: MatOfRect = image.attributes.get(AFImage.DOOR_ATTRIBUTE_NAME) as MatOfRect
        val foundDoorsArray = foundDoors.toArray()

        for (i in 0..foundDoors.rows() - 1) {
            val door = foundDoorsArray[i]
            val doorPoints = mutableListOf<Point>()
            sparsePoints.forEach { point: Point ->
                if (point.x < door.x + door.width + (door.width* ADDDETECTRATIO) && point.x > door.x - (door.width* ADDDETECTRATIO) && point.y < door.y + door.height + (door.height* ADDDETECTRATIO) && point.y > door.y - (door.height*ADDDETECTRATIO)) {
                    doorPoints.add(point)
                }
            }

            val angles = Array(doorPoints.size) { arrayOfNulls<Double>(doorPoints.size) }
            for (j in 0..doorPoints.size - 1) {
                for (k in (j + 1)..doorPoints.size - 1) {
                    angles[j][k] = angleToXAxis(doorPoints[j], doorPoints[k])
                    angles[k][j] = angleToXAxis(doorPoints[j], doorPoints[k])
                }
            }


            //Neu
            if (!angles.isEmpty()) {
                val size = angles[0].size - 1
                for (j in 0..size) {
                    for (k in (j + 1)..size) {
                        outerloop@ for (innerJ in j + 1..size) {
                            if (innerJ == k) continue@outerloop
                            innerloop@ for (innerK in (innerJ + 1)..size) {
                                if (innerK == k) continue@innerloop
                                if (innerJ != j && innerK != k && innerJ != k && innerK != j) {
                                    if ((angles[j][k] as Double).isApproximate(angles[innerJ][innerK] as Double, DOORCLOSINGANGLE)) {
                                        if ((angles[j][innerJ] as Double).isApproximate(angles[k][innerK] as Double, DOORCLOSINGANGLE)&& (angles[j][innerJ] as Double).isRectangular(angles[j][k] as Double, ERRORRECT)) {
                                            if(Math.abs(doorPoints[j].x - doorPoints[k].x)< DOORSIZEFACTOR *door.width && Math.abs(doorPoints[j].y - doorPoints[innerJ].y)< DOORSIZEFACTOR *door.height )
                                                Imgproc.rectangle(watershedoriginal, doorPoints[j], doorPoints[innerK], Scalar(NikieRoomDetection.BLACK), -1)
                                        } else if ((angles[j][innerK] as Double).isApproximate(angles[k][innerJ] as Double, DOORCLOSINGANGLE)&& (angles[j][innerK] as Double).isRectangular(angles[j][k] as Double, ERRORRECT)) {
                                            if(Math.abs(doorPoints[j].x - doorPoints[k].x)< DOORSIZEFACTOR *door.width && Math.abs(doorPoints[j].y - doorPoints[innerK].y)< DOORSIZEFACTOR *door.height )
                                                Imgproc.rectangle(watershedoriginal, doorPoints[j], doorPoints[innerK], Scalar(NikieRoomDetection.BLACK), -1)
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    private fun cornerDetection(localoriginal: Mat, watch: Stopwatch): Triple<Mat, Mat, MutableList<Point>> {
        println("${watch.elapsed().toTimeStamp()}\nCornerdetection")
        var cornerdet = localoriginal.copy().to8UC1()
        val cornerdetnorm = cornerdet.zeros()
        val cornerdetnormscaled = cornerdet.zeros()

        Imgproc.cornerHarris(cornerdet, cornerdet, BLOCKSIZE, KSIZE, K)
        Core.normalize(cornerdet, cornerdetnorm, ALPHA, BETA, Core.NORM_MINMAX, CvType.CV_32FC1, Mat())
        Core.convertScaleAbs(cornerdetnorm, cornerdetnormscaled)
        val threshhigh = CORNERMIN
        val points = mutableListOf<Point>()
        // Drawing a circle around corners
        for (j in 0..cornerdetnorm.rows() - 1) {
            var text = ""
            for (i in 0..cornerdetnorm.cols() - 1) {
                text += cornerdetnormscaled.get(j, i)[0].toInt().toString() + " "
                val point = cornerdetnorm.get(j, i)[0]
                if (point > threshhigh) {
                    points.add(Point(i.toDouble(), j.toDouble()))
                }
            }
        }

        println("found ${points.size} corners!")

        println("${watch.elapsed().toTimeStamp()}\nSparsing Points")
        val sparsePoints = points.sparsePoints(distance1.toDouble()).combinePoints()

        println("sparsed point cloud to ${sparsePoints.size} points!")

        for (p in sparsePoints)
            Imgproc.circle(cornerdetnormscaled, p, RADIUS, Scalar(BLACK, BLACK,WHITE))
        return Triple(cornerdet, cornerdetnormscaled, sparsePoints)
    }


    fun angleToXAxis(point1: Point, point2: Point): Double {
        val delta = Point(point1.x - point2.x, point1.y - point2.y)
        return -Math.atan(delta.y / delta.x)
    }

}