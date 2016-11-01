package ch.fhnw.afpars.algorithm.roomdetection

import ch.fhnw.afpars.algorithm.AlgorithmParameter
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.util.zeros
import org.opencv.core.MatOfPoint
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class RectangleRoomDetection : IRoomDetectionAlgorithm {
    override val name: String
        get() = "Alex Test Algorithm"

    @AlgorithmParameter(name = "Threshhold1", minValue = 0.0, maxValue = 300.0)
    var threshHold1 = 33.0

    @AlgorithmParameter(name = "Threshhold2", minValue = 0.0, maxValue = 300.0)
    var threshHold2 = 66.0

    @AlgorithmParameter(name = "ApertureSize", minValue = 1.0, maxValue = 7.0, majorTick = 2.0)
    var apertureSize = 3

    override fun run(image: AFImage, history: MutableList<AFImage>): AFImage {
        val img = image.clone()

        Imgproc.cvtColor(img.image, img.image, Imgproc.COLOR_BGR2GRAY)
        //Imgproc.threshold(img.image, img.image, 128.0, 255.0, Imgproc.THRESH_BINARY)
        //Imgproc.blur(img.image, img.image, Size(7.0, 7.0))
        Imgproc.GaussianBlur(img.image, img.image, Size(11.0, 11.0), 0.0, 0.0)
        val otsu_thresh_val = Imgproc.threshold(img.image, img.image, 0.0, 255.0, Imgproc.THRESH_OTSU);
        System.out.println(otsu_thresh_val)
        //Imgproc.threshold(img.image, img.image, 1.0, 128.0, Imgproc.THRESH_BINARY_INV)
        Imgproc.Canny(img.image, img.image, threshHold1, threshHold2, apertureSize, true)
        var hierarchy = img.image.zeros()
        var contours = mutableListOf<MatOfPoint>()
        Imgproc.findContours(img.image, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE)
        var outimg = img.image.zeros()
        Imgproc.threshold(outimg, outimg, 128.0, 255.0, Imgproc.THRESH_BINARY_INV)
        for (cnt in contours) {

            Imgproc.drawContours(outimg, mutableListOf(cnt), 0, Scalar(0.0, 0.0, 255.0), 2)

        }
        history.add(AFImage(img.image, "Input"))
        return AFImage(outimg);
    }
}