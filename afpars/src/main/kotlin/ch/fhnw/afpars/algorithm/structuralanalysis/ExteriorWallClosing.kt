package ch.fhnw.afpars.algorithm.structuralanalysis

import ch.fhnw.afpars.algorithm.AlgorithmParameter
import ch.fhnw.afpars.algorithm.IAlgorithm
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.util.*
import ch.fhnw.afpars.util.opencv.contour.Contour
import ch.fhnw.afpars.util.opencv.contour.ContourResult
import org.opencv.core.MatOfPoint
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

/**
 * Created by cansik on 17.02.17.
 */
class ExteriorWallClosing : IAlgorithm {

    @AlgorithmParameter(name = "Threshold", minValue = 0.0, maxValue = 255.0)
    var treshold = 128.0

    @AlgorithmParameter(name = "Wall Thickness", minValue = 1.0, maxValue = 50.0)
    var thickness = 10.0

    @AlgorithmParameter(name = "Approximation Weight", minValue = 0.0, maxValue = 0.5, majorTick = 0.01)
    var weight = 0.00

    override val name: String
        get() = "Exterior Wall Closing"

    override fun run(image: AFImage, history: MutableList<AFImage>): AFImage {

        // negate image
        val gray = image.image.copy()
        Imgproc.cvtColor(gray, gray, Imgproc.COLOR_BGR2GRAY)
        gray.negate()
        gray.threshold(treshold)

        val contourOutput = image.image.copy()
        val contours = gray.findContours()

        contours.drawContours(contourOutput, color = Scalar(0.0, 255.0, 0.0))

        // create big contour
        val wallMat = MatOfPoint(*contours.contours.flatMap { it.nativeContour.toList() }.toTypedArray())
        val wallContour = Contour(wallMat)
        val hull = wallContour.convexHullPoints()

        // output result
        hull.drawPolyLine(contourOutput, color = Scalar(255.0, 0.0, 255.0))

        // approximate polydp
        val approxHull = hull.approxPolyDP(weight = weight)
        approxHull.drawPolyLine(contourOutput, color = Scalar(0.0, 255.0, 255.0))

        // draw convex hull around building
        approxHull.drawPolyLine(image.image, color = Scalar(0.0), thickness = thickness.toInt())

        history.add(AFImage(gray, "negate"))
        history.add(AFImage(contourOutput, "contours"))

        return image.clone()
    }
}