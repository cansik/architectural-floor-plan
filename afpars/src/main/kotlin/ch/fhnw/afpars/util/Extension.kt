package ch.fhnw.afpars.util

import ch.fhnw.afpars.model.AFImage
import javafx.scene.image.Image
import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.io.ByteArrayInputStream

fun Mat.toImage(): Image {
    val byteMat = MatOfByte()
    Imgcodecs.imencode(".bmp", this, byteMat)
    return Image(ByteArrayInputStream(byteMat.toArray()))
}

fun Mat.zeros(): Mat {
    return Mat.zeros(this.rows(), this.cols(), this.type())
}

fun Mat.copy(): Mat {
    val m = this.zeros()
    this.copyTo(m)
    return m
}

fun AFImage.resize(width: Int, height: Int): AFImage {
    assert(width > 0 || height > 0)

    val result = this.clone()
    var w = width
    var h = height

    if (width == 0) {
        w = ((height.toDouble() / this.image.height()) * this.image.width()).toInt()
    }

    if (height == 0) {
        h = ((width.toDouble() / this.image.width()) * this.image.height()).toInt()
    }

    result.image = Mat.zeros(h, w, this.image.type())
    Imgproc.resize(this.image, result.image, result.image.size())
    return result
}

fun Mat.geodesicDilate(mask: Mat, elementSize: Int) {
    this.geodesicDilate(mask, elementSize, this)
}

fun Mat.geodesicDilate(mask: Mat, elementSize: Int, dest: Mat) {
    val img = this.clone()
    val element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(2.0 * elementSize + 1.0, 2.0 * elementSize + 1.0))

    var last = img.zeros()
    val next = img.copy()
    do {
        last = next.copy()
        Imgproc.dilate(last, next, element)
        Core.min(next, mask, next)
    } while (Core.norm(last, next) > 0.0001)

    last.copyTo(dest)
}

fun Mat.geodesicErode(mask: Mat, elementSize: Int) {
    this.geodesicErode(mask, elementSize, this)
}

fun Mat.geodesicErode(mask: Mat, elementSize: Int, dest: Mat) {
    val img = this.clone()
    val element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(2.0 * elementSize + 1.0, 2.0 * elementSize + 1.0))

    Imgproc.dilate(this, img, element)
    Core.min(img, mask, img)
    img.copyTo(dest)
}

fun Mat.negate() {
    this.negate(this)
}

fun Mat.negate(dest: Mat) {
    val invertedColorMatrix = this.zeros().setTo(Scalar(255.0))
    Core.subtract(invertedColorMatrix, this, dest)
}