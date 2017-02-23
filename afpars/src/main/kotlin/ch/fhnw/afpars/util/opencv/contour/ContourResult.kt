package ch.fhnw.afpars.util.opencv.contour

import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

/**
 * Created by cansik on 14.02.17.
 */
class ContourResult(nativeContours: MutableList<MatOfPoint>, val hierarchy: Mat) {
    val contours = nativeContours.map(::Contour).toMutableList()

    fun drawContours(dest: Mat, index: Int = -1, color: Scalar = Scalar(0.0, 0.0, 255.0), thickness : Int = 2) {
        Imgproc.drawContours(dest, contours.map { it.nativeContour }, index, color, thickness)
    }

    fun release() {
        contours.forEach { it.release() }
        hierarchy.release()
    }
}