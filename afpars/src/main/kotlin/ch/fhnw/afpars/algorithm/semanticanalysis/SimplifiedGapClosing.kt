package ch.fhnw.afpars.algorithm.semanticanalysis

import ch.fhnw.afpars.algorithm.AlgorithmParameter
import ch.fhnw.afpars.algorithm.IAlgorithm
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.util.*
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

class SimplifiedGapClosing :IAlgorithm{
    override val name: String
        get() = "Simplified Gap Closing Algorithm"

    @AlgorithmParameter(name = "Threshold", helpText = "Splits image into black and white.", minValue = 0.0, maxValue = 255.0)
    var threshold = 128.0

    @AlgorithmParameter(name = "Opening", minValue = 0.0, maxValue = 20.0)
    var openingSize = 1

    @AlgorithmParameter(name = "Closing")
    var closingSize = 34

    @AlgorithmParameter(name = "Offset %", minValue = 0.0, maxValue = 1.0, majorTick = 0.01)
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
            val offsetX = offset * rect.width
            val offsetY = offset * rect.height

            var newXPoint = rect.x - offsetX.toInt()
            var newYPoint = rect.y - offsetY.toInt()
            var newXHeight = rect.width + (offsetX * 2.0).toInt()
            var newYHeight = rect.height + (offsetY * 2.0).toInt()
            if(newXPoint<0){ newXPoint =0}
            if(newXPoint>original.image.width()){newXPoint =0}
            if(newYPoint<0){ newYPoint =0}
            if(newYPoint>original.image.height()){newYPoint =0}
            if(newXPoint+newXHeight<0){ newXHeight =0}
            if(newXPoint+newXHeight>original.image.width()){newXHeight = original.image.width()-newXPoint}
            if(newYPoint+newYHeight<0){ newYHeight =0}
            if(newYPoint+newYHeight>original.image.height()){
                newYHeight =original.image.height()-newYPoint
            }

            /*
            val offsetRect = Rect(rect.x - offsetX.toInt(),
                                rect.y - offsetY.toInt(),
                                rect.width + (offsetX * 2.0).toInt(),
                                rect.height + (offsetY * 2.0).toInt())*/
            val offsetRect = Rect(newXPoint,
                    newYPoint,
                    newXHeight,
                    newYHeight)

            val part = Mat(original.image, offsetRect)
            val thresholdImage = part.zeros()

            Imgproc.cvtColor(part, thresholdImage, Imgproc.COLOR_BGR2GRAY)
            thresholdImage.threshold(threshold, type = Imgproc.THRESH_BINARY)

            // opening
            thresholdImage.dilate(openingSize)
            thresholdImage.erode(openingSize)

            // closing
            thresholdImage.erode(closingSize)
            thresholdImage.dilate(closingSize)

            // convert back to bgr
            Imgproc.cvtColor(thresholdImage, thresholdImage, Imgproc.COLOR_GRAY2BGR)
            thresholdImage.copyTo(Mat(original.image, offsetRect))

            // visualise result in output
            thresholdImage.replaceColor(Scalar(0.0, 0.0, 0.0), Scalar(0.0, 0.0, 255.0))
            thresholdImage.copyTo(Mat(offsetRectImage, offsetRect))

            // draw rect
            Imgproc.rectangle(offsetRectImage, offsetRect.tl(), offsetRect.br(), Scalar(0.0, 255.0, 0.0), 2)
        }

        history.add(AFImage(offsetRectImage, "offset"))

        return original
    }
}