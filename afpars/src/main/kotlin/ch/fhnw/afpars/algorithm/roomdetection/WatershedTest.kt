package ch.fhnw.afpars.algorithm.roomdetection

import ch.fhnw.afpars.algorithm.AlgorithmParameter
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.util.copy
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

/**
 * Created by cansik on 16.10.16.
 */
class WatershedTest : IRoomDetectionAlgorithm {
    @AlgorithmParameter(name = "Threshold", minValue = 0.0, maxValue = 255.0)
    var treshold = 100.0

    override fun run(image: AFImage, history: MutableList<AFImage>): AFImage {
        val mRgba = image.image

        history.add(AFImage(mRgba.copy(), "Original"))

        val threeChannel = Mat()
        Imgproc.cvtColor(mRgba, threeChannel, Imgproc.COLOR_BGR2GRAY)
        Imgproc.threshold(threeChannel, threeChannel, treshold, 255.0, Imgproc.THRESH_BINARY)

        history.add(AFImage(threeChannel.copy(), "Threshold"))

        val fg = Mat(mRgba.size(), CvType.CV_8U)
        Imgproc.erode(threeChannel, fg, Mat(), Point(-1.0, -1.0), 2)

        history.add(AFImage(fg.copy(), "Foreground"))

        val bg = Mat(mRgba.size(), CvType.CV_8U)
        Imgproc.dilate(threeChannel, bg, Mat(), Point(-1.0, -1.0), 3)
        Imgproc.threshold(bg, bg, 1.0, 128.0, Imgproc.THRESH_BINARY_INV)

        history.add(AFImage(bg.copy(), "Background"))

        val markers = Mat(mRgba.size(), CvType.CV_8U, Scalar(0.0))
        Core.add(fg, bg, markers)

        history.add(AFImage(markers.copy(), "Markers"))

        val segmenter = WatershedSegmenter(markers)
        val result = segmenter.process(mRgba)

        return AFImage(result)
    }

    override val name: String
        get() = "Watershed Test"

    inner class WatershedSegmenter(var markers: Mat) {

        init {
            markers.convertTo(markers, CvType.CV_32S)
        }

        fun process(image: Mat): Mat {
            Imgproc.watershed(image, markers)
            markers.convertTo(markers, CvType.CV_8U)
            return markers
        }
    }
}