package ch.fhnw.afpars.util

import javafx.scene.image.Image
import org.bytedeco.javacpp.opencv_core
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
    return this.zeros(this.type())
}

fun Mat.zeros(type: Int): Mat {
    return Mat.zeros(this.rows(), this.cols(), type)
}

fun Mat.copy(): Mat {
    val m = this.zeros()
    this.copyTo(m)
    return m
}

fun Mat.resize(width: Int, height: Int): Mat {
    assert(width > 0 || height > 0)

    var w = width
    var h = height

    if (width == 0) {
        w = ((height.toDouble() / this.height()) * this.width()).toInt()
    }

    if (height == 0) {
        h = ((width.toDouble() / this.width()) * this.height()).toInt()
    }

    val result = Mat.zeros(h, w, this.type())
    Imgproc.resize(this, result, result.size())
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

fun Mat.convertToJavaCV(): opencv_core.Mat {
    val jcvmat = opencv_core.Mat(this.rows(),this.cols(),CvType.CV_8UC3)
    val return_buff = ByteArray((this.total() * this.channels()).toInt())
    this.get(0, 0, return_buff)
    jcvmat.data().put(*return_buff)
    return jcvmat
}

fun MatOfPoint.approxPolyDP(epsilon: Double? = null): MatOfPoint {
    val cont = MatOfPoint2f(this.to32FC2())
    val approxDoorContour2f = MatOfPoint2f()

    var eps = epsilon
    if (eps == null)
        eps = 0.01 * Imgproc.arcLength(cont, true)

    Imgproc.approxPolyDP(cont, approxDoorContour2f, eps, true)
    return MatOfPoint(approxDoorContour2f.to32S())
}

fun opencv_core.KeyPointVector.convertToOpenCV():MatOfKeyPoint{
    val matofkp = MatOfKeyPoint()
    val keyptlist = mutableListOf<KeyPoint>()
    for(i in 0..this.size()){
        keyptlist.add(KeyPoint(this.get(i).pt().x(),this.get(i).pt().y(),this.get(i).angle(),this.get(i).response(),this.get(i).octave().toFloat(),this.get(i).class_id()))
    }
    matofkp.fromList(keyptlist)
    return matofkp
}
