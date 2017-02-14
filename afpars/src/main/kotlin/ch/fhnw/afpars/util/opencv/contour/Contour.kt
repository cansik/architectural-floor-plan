package ch.fhnw.afpars.util.opencv.contour

import ch.fhnw.afpars.util.approxPolyDP
import ch.fhnw.afpars.util.convexHull
import ch.fhnw.afpars.util.toMatOfPoint2f
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.imgproc.Moments

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

    fun isOnBorder(image : Mat, approxDelta: Double = 0.0)
    {
        isOnBorder(approxDelta,
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

    fun release() {
        nativeContour.release()
        contour2F.release()
    }
}