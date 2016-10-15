package ch.fhnw.afpars.util

import ch.fhnw.afpars.model.AFImage
import javafx.scene.image.Image
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.io.ByteArrayInputStream

fun Mat.toImage(): Image {
    val byteMat = MatOfByte()
    Imgcodecs.imencode(".bmp", this, byteMat)
    return Image(ByteArrayInputStream(byteMat.toArray()))
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