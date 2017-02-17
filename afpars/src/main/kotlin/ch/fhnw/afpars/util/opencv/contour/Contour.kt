package ch.fhnw.afpars.util.opencv.contour

import ch.fhnw.afpars.util.approxPolyDP
import ch.fhnw.afpars.util.convexHull
import ch.fhnw.afpars.util.toMatOfPoint2f
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.imgproc.Moments
import javax.swing.Spring.height
import org.opencv.core.MatOfPoint



/**
 * Created by cansik on 14.02.17.
 */
class Contour(val nativeContour: MatOfPoint) {

    private val contour2F: MatOfPoint2f by lazy { nativeContour.toMatOfPoint2f() }

    fun moments(): Moments {
        return Imgproc.moments(nativeContour)
    }

    fun area(oriented: Boolean = false): Double {
        return Imgproc.contourArea(nativeContour, oriented)
    }

    fun arcLength(closed: Boolean = false): Double {
        return Imgproc.arcLength(contour2F, closed)
    }

    fun approxPolyDP(epsilon: Double): MatOfPoint {
        return nativeContour.approxPolyDP(epsilon)
    }

    fun convexHull(clockwise: Boolean = true): MatOfInt {
        return nativeContour.convexHull(clockwise)
    }

    fun convexHullPoints(clockwise: Boolean = true) : MatOfPoint
    {
        val hull = convexHull(clockwise)

        val mopOut = MatOfPoint()
        mopOut.create(hull.size().height.toInt(), 1, CvType.CV_32SC2)

        for (i in 0..hull.size().height.toInt() - 1) {
            val point = doubleArrayOf(nativeContour.get(hull.get(i, 0)[0].toInt(), 0)[0], nativeContour.get(hull.get(i, 0)[0].toInt(), 0)[1])
            mopOut.put(i, 0, *point)
        }

        return mopOut
    }

    fun isContourConvex(): Boolean {
        return Imgproc.isContourConvex(nativeContour)
    }

    fun boundingBox(): Rect {
        return Imgproc.boundingRect(nativeContour)
    }

    fun minAreaBox(): MatOfPoint2f {
        val boxPoints = MatOfPoint2f()
        Imgproc.boxPoints(Imgproc.minAreaRect(contour2F), boxPoints)
        return boxPoints
    }

    fun isOnBorder(image : Mat, approxDelta: Double = 0.0) : Boolean
    {
        return isOnBorder(approxDelta,
                approxDelta,
                image.width().toDouble() - approxDelta,
                image.height().toDouble() - approxDelta)
    }

    fun isOnBorder(minX : Double, minY : Double, maxX : Double, maxY : Double) : Boolean
    {
        nativeContour.toArray().forEach {
            if(it.x <= minX || it.x >= maxX || it.y <= minY || it.y >= maxY)
                return true
        }

        return false
    }

    fun drawContour(dest: Mat, color: Scalar = Scalar(0.0, 0.0, 255.0)) {
        Imgproc.drawContours(dest, listOf(nativeContour), -1, color)
    }

    fun release() {
        nativeContour.release()
        contour2F.release()
    }
}