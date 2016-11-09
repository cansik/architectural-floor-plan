package ch.fhnw.afpars.algorithm.areadetection

import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.util.convert
import ch.fhnw.afpars.util.zeros
import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc

/**
 * Created by cansik on 08.11.16.
 */
class VectorDoorDetection : IAreaDetectionAlgorithm {
    override fun run(image: AFImage, history: MutableList<AFImage>): AFImage {

        /*
        val houghTransform = HoughTransform()
        val houghImage = houghTransform.run(image)
        val lines = houghImage.attributes["houghlines"]

        val dest = Mat(Size(image.image.width().toDouble(), image.image.height().toDouble()), 0)
        dest.setTo(Scalar(255.0, 255.0, 255.0))

        for (i in 0..lines!!.size().height.toInt() - 1) {
            //Exakte Methode für Edge-Linien, hat evtl zu viele Linien
            val line = lines.get(i, 0)
            val pt1 = Point(line[0], line[1])
            val pt2 = Point(line[2], line[3])
            Imgproc.line(dest, pt1, pt2, Scalar(0.0, 0.0, 255.0), 3)
        }

        history.add(AFImage(dest, "Hough"))
        */

        val img = image.image
        val templ = Imgcodecs.imread("data/door.png")
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
        if (match_method === Imgproc.TM_SQDIFF || match_method === Imgproc.TM_SQDIFF_NORMED) {
            matchLoc = mmr.minLoc
        } else {
            matchLoc = mmr.maxLoc
        }

        // / Show me what you got
        Imgproc.rectangle(img, matchLoc, Point(matchLoc.x + templ.cols(),
                matchLoc.y + templ.rows()), Scalar(0.0, 255.0, 0.0))

        return AFImage(img, "Result")
    }

    fun combineLines(lines: Mat, maxDistance: Double): List<AFLine> {
        val combined = mutableListOf<AFLine>()

        // add marker column
        lines.reshape(lines.cols() + 1, lines.rows())

        for (r in 0..lines.rows()) {

            // check if already marked
            if (lines[r, 4][0] == 1.0)
                continue

            // mark this one
            lines[r, 4][0] = 1.0

            // search nearest line for start and endpoint

        }

        return combined
    }

    class AFLine(p1: AFPoint, p2: AFPoint) {

    }

    class AFPoint(x: Double, y: Double) {

    }

    override val name: String
        get() = "Vector Door Detection"
}