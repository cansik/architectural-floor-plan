package ch.fhnw.afpars.util

import ch.fhnw.afpars.util.opencv.cc.ConnectedComponentsResult
import ch.fhnw.afpars.util.opencv.contour.ContourResult
import javafx.scene.image.Image
import javafx.scene.image.WritablePixelFormat
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

fun Mat.gray() {
    Imgproc.cvtColor(this, this, Imgproc.COLOR_BGR2GRAY)
}

fun Mat.threshold(thresh: Double, maxval: Double = 255.0, type: Int = Imgproc.THRESH_BINARY) {
    Imgproc.threshold(this, this, thresh, maxval, type)
}

fun Mat.erode(erosionSize: Int) {
    if(erosionSize == 0)
        return

    val structureSize = erosionSize + if ((erosionSize % 2) == 0) 1 else 0
    val element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(structureSize.toDouble(), structureSize.toDouble()))
    Imgproc.erode(this, this, element)
}

fun Mat.dilate(dilationSize: Int) {
    if(dilationSize == 0)
        return

    val structureSize = dilationSize + if ((dilationSize % 2) == 0) 1 else 0
    val element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(structureSize.toDouble(), structureSize.toDouble()))
    Imgproc.dilate(this, this, element)
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

fun Mat.sharpen(sigmaX: Double = 3.0) {
    this.sharpen(this, sigmaX)
}

fun Mat.sharpen(dest: Mat, sigmaX: Double = 3.0) {
    if(sigmaX == 0.0)
        this.copyTo(dest)

    val blurred = Mat()
    Imgproc.GaussianBlur(this, blurred, Size(0.0, 0.0), sigmaX)
    Core.addWeighted(this, 1.5, blurred, -0.5, 0.0, dest)
}

fun Mat.negate(dest: Mat) {
    val invertedColorMatrix = this.zeros().setTo(Scalar(255.0))
    Core.subtract(invertedColorMatrix, this, dest)
}

fun Mat.convertToJavaCV(): opencv_core.Mat {
    val jcvmat = opencv_core.Mat(this.rows(), this.cols(), CvType.CV_8UC3)
    val return_buff = ByteArray((this.total() * this.channels()).toInt())
    this.get(0, 0, return_buff)
    jcvmat.data().put(*return_buff)
    return jcvmat
}

fun MatOfPoint.approxPolyDP(epsilon: Double? = null, weight : Double = 0.01): MatOfPoint {
    val cont = MatOfPoint2f(this.to32FC2())
    val approxDoorContour2f = MatOfPoint2f()

    var eps = epsilon
    if (eps == null)
        eps = weight * Imgproc.arcLength(cont, true)

    Imgproc.approxPolyDP(cont, approxDoorContour2f, eps, true)
    return MatOfPoint(approxDoorContour2f.to32S())
}

fun MatOfPoint.convexHull(clockwise: Boolean = false): MatOfInt {
    val result = MatOfInt()
    Imgproc.convexHull(this, result, clockwise)
    return result
}

fun Mat.findContours(method: Int = Imgproc.CHAIN_APPROX_SIMPLE): ContourResult {
    val nativeContours = mutableListOf<MatOfPoint>()
    val hierarchy = Mat()

    Imgproc.findContours(this.copy(), nativeContours, hierarchy, Imgproc.RETR_TREE, method)

    return ContourResult(nativeContours, hierarchy)
}

fun opencv_core.KeyPointVector.convertToOpenCV(): MatOfKeyPoint {
    val matofkp = MatOfKeyPoint()
    val keyptlist = mutableListOf<KeyPoint>()
    for (i in 0..this.size()) {
        keyptlist.add(KeyPoint(this.get(i).pt().x(), this.get(i).pt().y(), this.get(i).angle(), this.get(i).response(), this.get(i).octave().toFloat(), this.get(i).class_id()))
    }
    matofkp.fromList(keyptlist)
    return matofkp
}

fun Double.isApproximate(value: Double, error: Double): Boolean {
    return (Math.abs(Math.abs(this) - Math.abs(value)) < error)
}

fun Double.isRectangular(value:Double,error: Double):Boolean{
    val result = Math.abs(Math.abs(this) - Math.abs(value))
    return result > Math.PI/2-error && result < Math.PI/2+error
}

fun Image.toMat(): Mat {
    val width = this.width.toInt()
    val height = this.height.toInt()
    val buffer = ByteArray(width * height * 4)

    val reader = this.pixelReader
    val format = WritablePixelFormat.getByteBgraInstance()
    reader.getPixels(0, 0, width, height, format, buffer, 0, width * 4)

    val mat = Mat(height, width, CvType.CV_8UC4)
    mat.put(0, 0, buffer)
    return mat
}

fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

fun Mat.connectedComponents(connectivity: Int = 8, ltype: Int = CvType.CV_32S): Mat {
    val labeled = this.zeros()
    Imgproc.connectedComponents(this, labeled, connectivity, ltype)
    return labeled
}

fun Mat.connectedComponentsWithStats(connectivity: Int = 8, ltype: Int = CvType.CV_32S): ConnectedComponentsResult {
    val labeled = this.zeros()
    val rectComponents = Mat()
    val centComponents = Mat()

    Imgproc.connectedComponentsWithStats(this, labeled, rectComponents, centComponents)
    return ConnectedComponentsResult(labeled, rectComponents, centComponents)
}

fun Mat.getRegionMask(regionLabel: Int): Mat {
    val labeledMask = this.zeros(CvType.CV_8U)
    Core.inRange(this, Scalar(regionLabel.toDouble()), Scalar(regionLabel.toDouble()), labeledMask)
    return labeledMask
}

fun Long.toTimeStamp(): String {
    val second = this / 1000 % 60
    val minute = this / (1000 * 60) % 60
    val hour = this / (1000 * 60 * 60) % 24

    return String.format("%02d:%02d:%02d:%d", hour, minute, second, this)
}

fun Mat.replaceColor(oldColor: Scalar, newColor: Scalar) {
    this.replaceColor(oldColor, newColor, this)
}

fun Mat.replaceColor(oldColor: Scalar, newColor: Scalar, dest: Mat) {
    val mask = this.zeros()
    val ncimage = Mat(this.size(), dest.type(), newColor)

    Core.inRange(this, oldColor, oldColor, mask)
    ncimage.copyTo(dest, mask)
}

fun MatOfPoint.toMatOfPoint2f(): MatOfPoint2f {
    val dst = MatOfPoint2f()
    this.convertTo(dst, CvType.CV_32F)
    return dst
}


fun MatOfPoint.drawPolyLine(img : Mat, closed : Boolean = true, color : Scalar = Scalar(0.0), thickness :Int = 2)
{
    Imgproc.polylines(img, listOf(this), closed, color, thickness)
}

fun MatOfPoint.fillPoly(img : Mat, color : Scalar = Scalar(0.0))
{
    Imgproc.fillPoly(img, listOf(this), color)
}