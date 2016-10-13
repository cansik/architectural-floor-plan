package ch.fhnw.afpars.util

import ch.fhnw.afpars.model.AFImage
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

/**
 * Created by cansik on 13.10.16.
 */
fun AFImage.erode(erosionSize: Int): AFImage {
    val element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(2.0 * erosionSize + 1.0, 2.0 * erosionSize + 1.0))
    val result = AFImage(Mat())
    Imgproc.erode(this.image, result.image, element)
    return result
}

fun AFImage.dilate(dilationSize: Int): AFImage {
    val element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(2.0 * dilationSize + 1.0, 2.0 * dilationSize + 1.0))
    val result = AFImage(Mat())
    Imgproc.dilate(this.image, result.image, element)
    return result
}

fun AFImage.threshold(treshold: Double, maxValue: Double, type: Int): AFImage {
    val result = AFImage(Mat())
    Imgproc.threshold(this.image, result.image, treshold, maxValue, type)
    return result
}

fun AFImage.threshold(treshold: Double): AFImage {
    return this.threshold(treshold, 255.0, Imgproc.THRESH_BINARY)
}