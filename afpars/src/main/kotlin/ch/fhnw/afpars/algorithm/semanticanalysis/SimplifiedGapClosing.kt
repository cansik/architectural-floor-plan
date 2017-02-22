package ch.fhnw.afpars.algorithm.semanticanalysis

import ch.fhnw.afpars.algorithm.AlgorithmParameter
import ch.fhnw.afpars.algorithm.IAlgorithm
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.util.replaceColor
import ch.fhnw.afpars.util.zeros
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

class SimplifiedGapClosing :IAlgorithm{
    override val name: String
        get() = "Simplified Gap Closing Algorithm"

    @AlgorithmParameter(name = "Threshold", minValue = 0.0, maxValue = 255.0)
    var threshold = 128.0

    @AlgorithmParameter(name = "Opening", minValue = 0.0, maxValue = 20.0)
    var openingSize = 1

    @AlgorithmParameter(name = "Closing")
    var closingSize = 34

    @AlgorithmParameter(name = "Offset %", minValue = 0.0, maxValue = 1.0)
    var offset = 0.0

    /*
    Input ist ein morphologisch transformiertes Bild
    Eingabetyp ist 32SC1
     */
    override fun run(image: AFImage, history: MutableList<AFImage>): AFImage {
        val doors = image.attributes[AFImage.DOOR_ATTRIBUTE_NAME] as MatOfRect

        val original = image.clone()
        val offsetRectImage = image.image.clone()

        history.add(AFImage(original.image.clone(), "original"))

        loop@ for (rect in doors.toArray().sortedByDescending(org.opencv.core.Rect::area)) {
            val offsetRect = Rect(rect.x - (offset * rect.x).toInt(),
                                rect.y - (offset * rect.y).toInt(),
                                rect.width + (offset * rect.width).toInt(),
                                rect.height + (offset * rect.height).toInt())

            val part = Mat(original.image, offsetRect)
            val thresholdImage = part.zeros()

            Imgproc.cvtColor(part, thresholdImage, Imgproc.COLOR_BGR2GRAY)
            Imgproc.threshold(thresholdImage, thresholdImage, threshold, 255.0, Imgproc.THRESH_BINARY)

            // opening
            dilate(thresholdImage, openingSize)
            erode(thresholdImage, openingSize)

            // closing
            erode(thresholdImage, closingSize)
            dilate(thresholdImage, closingSize)

            // convert back to bgr
            Imgproc.cvtColor(thresholdImage, thresholdImage, Imgproc.COLOR_GRAY2BGR)
            thresholdImage.copyTo(Mat(original.image, offsetRect))

            // visualise result in output
            thresholdImage.replaceColor(Scalar(0.0, 0.0, 0.0), Scalar(0.0, 0.0, 255.0))
            thresholdImage.copyTo(Mat(offsetRectImage, offsetRect))

            // draw rect
            Imgproc.rectangle(offsetRectImage, offsetRect.tl(), offsetRect.br(), Scalar(0.0, 255.0, 0.0), 2)

            history.add(AFImage(thresholdImage, "door"))
        }

        return original
    }

    fun erode(img: Mat, erosionSize: Int) {
        val element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(2.0 * erosionSize + 1.0, 2.0 * erosionSize + 1.0))
        Imgproc.erode(img, img, element)
    }

    fun dilate(img: Mat, dilationSize: Int) {
        val element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(2.0 * dilationSize + 1.0, 2.0 * dilationSize + 1.0))
        Imgproc.dilate(img, img, element)
    }

    fun threshold(img: Mat, treshold: Double, maxValue: Double, type: Int) {
        Imgproc.threshold(img, img, treshold, maxValue, type)
    }

    fun threshold(img: Mat, treshold: Double) {
        this.threshold(img, treshold, 255.0, Imgproc.THRESH_BINARY)
    }
}