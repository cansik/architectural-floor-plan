package ch.fhnw.afpars.algorithm.informationsegmentation

import ch.fhnw.afpars.algorithm.AlgorithmParameter
import ch.fhnw.afpars.algorithm.IAlgorithm
import ch.fhnw.afpars.model.AFImage
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class RectangleRoomDetection : IAlgorithm {
    override val name: String
        get() = "Alex Test Algorithm"

    @AlgorithmParameter(name = "Threshhold1", minValue = 0.0, maxValue = 300.0)
    var threshHold1 = 33.0

    @AlgorithmParameter(name = "Threshhold2", minValue = 0.0, maxValue = 300.0)
    var threshHold2 = 66.0

    @AlgorithmParameter(name = "ApertureSize", minValue = 1.0, maxValue = 7.0, majorTick = 2.0)
    var apertureSize = 3

    override fun run(image: AFImage, history: MutableList<AFImage>): AFImage {
        val img = image.clone()

        //Canny
        Imgproc.cvtColor(img.image, img.image, Imgproc.COLOR_BGR2GRAY)
        Imgproc.GaussianBlur(img.image, img.image, Size(11.0, 11.0), 0.0, 0.0)
        val otsu_thresh_val = Imgproc.threshold(img.image, img.image, 0.0, 255.0, Imgproc.THRESH_OTSU)
        Imgproc.Canny(img.image, img.image, threshHold1, threshHold2, apertureSize, true)


        //HoughTransformation
        val lines = Mat()
        //Imgproc.threshold(img.image, img.image, 128.0, 255.0, Imgproc.THRESH_BINARY_INV)
        Imgproc.HoughLinesP(img.image, lines, 1.0, Math.PI / 180, 0)

        //Weisse Mat()
        val dest = Mat(Size(img.image.width().toDouble(), img.image.height().toDouble()), 0)
        dest.setTo(Scalar(255.0, 255.0, 255.0))

        //Linien einzeichnen
        for (i in 0..lines.size().height.toInt() - 1) {
            //Diese Methode funktioniert noch nicht

            /*val rho = lines.get(i,0).get(0)
            val theta = lines.get(i,0).get(1)
            val a = Math.cos(theta)
            val b = Math.sin(rho)
            val x0 = a*rho
            val y0 = b*rho
            val pt1 = Point(Math.round(x0+1000*(-b)).toDouble(),Math.round(y0+100*(a)).toDouble())
            val pt2 = Point(Math.round(x0-1000*(-b)).toDouble(),Math.round(y0-100*(a)).toDouble())*/

            //Exakte Methode für Edge-Linien, hat evtl zu viele Linien
            val line = lines.get(i, 0)
            val pt1 = Point(line.get(0), line.get(1))
            val pt2 = Point(line.get(2), line.get(3))
            Imgproc.line(dest, pt1, pt2, Scalar(0.0, 0.0, 255.0), 5)
        }

        //findContours
        /*var hierarchy = img.image.zeros()
        var contours = mutableListOf<MatOfPoint>()
        Imgproc.findContours(img.image, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE)
        var outimg = img.image.zeros()
        Imgproc.threshold(outimg, outimg, 128.0, 255.0, Imgproc.THRESH_BINARY_INV)
        for (cnt in contours) {

            Imgproc.drawContours(outimg, mutableListOf(cnt), 0, Scalar(0.0, 0.0, 255.0), 2)

        }
        history.add(AFImage(img.image, "Input"))
        return AFImage(outimg);*/
        return AFImage(dest);
    }
}