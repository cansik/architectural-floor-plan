package ch.fhnw.afpars.algorithm.base

import ch.fhnw.afpars.algorithm.IAlgorithm
import ch.fhnw.afpars.model.AFImage
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

/**
 * Created by cansik on 03.11.16.
 */
class HoughTransform : IAlgorithm {
    override fun run(image: AFImage, history: MutableList<AFImage>): AFImage {

        var destination = image.image

        //Canny Image
        //val dilation_size1 = 8
        //val element2 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(2.0 * dilation_size1 + 1.0, 2.0 * dilation_size1 + 1.0))
        //Imgproc.erode(destination,destination,element2)
        var canny = Mat()
        //Imgproc.blur(destination, canny, Size(3.0,3.0) );
        val threshlow = 1.0
        Imgproc.Canny(destination, canny, 50.0, 150.0);

        //HoughTransformation (result = nx4)
        val lines = Mat()
        Imgproc.HoughLinesP(canny, lines, 1.0, Math.PI / 180, 0)

        //Weisse Mat()
        val dest = Mat(Size(destination.width().toDouble(), destination.height().toDouble()), 0)
        dest.setTo(Scalar(255.0, 255.0, 255.0))

        //Linien einzeichnen
        /*
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
            Imgproc.line(dest, pt1, pt2, Scalar(0.0, 0.0, 255.0), 3)
        }
        */

        //Im moment wird eine Matrize mit allen Linien zurückgegeben.
        //Es könnte auch das ganze Bild zurückgegeben werden.
        image.attributes.put("houghlines", lines)
        return image
    }

    override val name: String
        get() = "Hough-Transform"
}