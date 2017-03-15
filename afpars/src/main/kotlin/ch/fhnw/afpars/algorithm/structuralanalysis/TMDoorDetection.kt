package ch.fhnw.afpars.algorithm.structuralanalysis

import ch.fhnw.afpars.algorithm.AlgorithmParameter
import ch.fhnw.afpars.algorithm.IAlgorithm
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.util.*
import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc

/**
 * Created by cansik on 08.11.16.
 */
class TMDoorDetection : IAlgorithm {
    @AlgorithmParameter(name = "Threshold", minValue = 0.0, maxValue = 255.0)
    var treshold = 250.0

    override fun run(image: AFImage, history: MutableList<AFImage>): AFImage {
        val img = image.image
        val templ = Imgcodecs.imread("template/door.png")
        val match_method = Imgproc.TM_CCOEFF_NORMED

        history.add(AFImage(templ, "Template"))

        // / Create the result matrix
        val result_cols = img.cols() - templ.cols() + 1
        val result_rows = img.rows() - templ.rows() + 1
        val result = Mat(result_rows, result_cols, CvType.CV_32FC1)

        // / Do the Matching and Normalize
        Imgproc.matchTemplate(img, templ, result, match_method)
        Core.normalize(result, result, 0.0, 1.0, Core.NORM_MINMAX, -1, Mat())

        val corrleationMap = result.zeros()
        Core.multiply(result, Scalar(255.0), corrleationMap)

        history.add(AFImage(corrleationMap.convert(CvType.CV_8U), "Matching"))

        // / Localizing the best match with minMaxLoc
        val mmr = Core.minMaxLoc(result)

        val matchLoc: Point
        if (match_method == Imgproc.TM_SQDIFF || match_method == Imgproc.TM_SQDIFF_NORMED) {
            matchLoc = mmr.minLoc
        } else {
            matchLoc = mmr.maxLoc
        }

        // / Show me what you got
        Imgproc.rectangle(img, Point(matchLoc.x + templ.width(),
                matchLoc.y + templ.height()), matchLoc, Scalar(0.0, 255.0, 0.0))

        // find multiple door results
        val gray = corrleationMap.convert(CvType.CV_8U).copy()
        gray.threshold(treshold)

        val contourOutput = image.image.copy()
        val contours = gray.findContours()

        contours.drawContours(contourOutput, color = Scalar(0.0, 0.0, 255.0), thickness = 3)

        history.add(AFImage(gray, "gray"))
        history.add(AFImage(contourOutput, "doors (${contours.contours.size})"))

        return AFImage(img, "Result")
    }

    override val name: String
        get() = "Vector Door Detection"
}