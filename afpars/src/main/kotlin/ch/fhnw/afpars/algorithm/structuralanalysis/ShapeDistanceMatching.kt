package ch.fhnw.afpars.algorithm.structuralanalysis

import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.util.approxPolyDP
import ch.fhnw.afpars.util.zeros
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import java.util.*


/**
 * Created by cansik on 22.12.16.
 */
class ShapeDistanceMatching : ch.fhnw.afpars.algorithm.IAlgorithm {
    override val name: String
        get() = "BaseShape Distance Matching"

    override fun run(image: ch.fhnw.afpars.model.AFImage, history: MutableList<ch.fhnw.afpars.model.AFImage>): ch.fhnw.afpars.model.AFImage {
        val areas = org.opencv.core.MatOfRect(image.attributes[AFImage.doorName])

        // load default door contour
        val doorTemplate = org.opencv.imgcodecs.Imgcodecs.imread("data/door.png")
        val thresholdDoor = doorTemplate.zeros()

        org.opencv.imgproc.Imgproc.cvtColor(doorTemplate, thresholdDoor, org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY)
        org.opencv.imgproc.Imgproc.threshold(thresholdDoor, thresholdDoor, 128.0, 255.0, org.opencv.imgproc.Imgproc.THRESH_BINARY_INV)

        val doorContours = java.util.ArrayList<org.opencv.core.MatOfPoint>()
        org.opencv.imgproc.Imgproc.findContours(thresholdDoor, doorContours, org.opencv.core.Mat(), org.opencv.imgproc.Imgproc.RETR_LIST, org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE)

        // approx contour
        val doorContour = doorContours[0].approxPolyDP()
        val doorArea = org.opencv.imgproc.Imgproc.contourArea(doorContour)

        org.opencv.imgproc.Imgproc.drawContours(doorTemplate, listOf(doorContour), -1, org.opencv.core.Scalar(255.0, 0.0, 0.0))
        history.add(ch.fhnw.afpars.model.AFImage(doorTemplate, "Door Template"))

        val doors = mutableListOf<Pair<Double, org.opencv.core.Mat>>()

        // process all segments
        loop@ for (rect in areas.toArray().sortedByDescending(org.opencv.core.Rect::area)) {
            val part = Mat(image.image, rect)
            val thresholdImage = part.zeros()

            Imgproc.cvtColor(part, thresholdImage, Imgproc.COLOR_BGR2GRAY)
            Imgproc.threshold(thresholdImage, thresholdImage, 128.0, 255.0, Imgproc.THRESH_BINARY_INV)

            val allContours = ArrayList<MatOfPoint>()
            Imgproc.findContours(thresholdImage, allContours, Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE)

            // filter all small contours
            val contours = allContours.filter { Imgproc.contourArea(it) > 0.2 * doorArea }.toList()

            // skip all parts without contours
            if (contours.size == 0)
                continue@loop

            // go through every contour and select the best matching
            var bestCont = contours[0]
            var bestMatch = Double.MAX_VALUE

            for (cont in contours) {
                val approx = cont.approxPolyDP()
                val match = Imgproc.matchShapes(doorContour, approx, Imgproc.CV_CONTOURS_MATCH_I1, 0.0)

                if (match < bestMatch) {
                    bestCont = approx
                    bestMatch = match
                }
            }

            Imgproc.drawContours(part, contours, -1, Scalar(0.0, 0.0, 255.0))

            // draw best matching contour blue
            Imgproc.drawContours(part, listOf(bestCont), -1, Scalar(255.0, 0.0, 0.0))
            doors.add(Pair(bestMatch, part))
        }

        doors.sortByDescending { it.first }

        doors.mapTo(history) { AFImage(it.second, "Match: ${it.first}") }

        return image
    }
}