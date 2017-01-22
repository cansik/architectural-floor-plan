package ch.fhnw.afpars.algorithm.semanticanalysis

import ch.fhnw.afpars.algorithm.AlgorithmParameter
import ch.fhnw.afpars.algorithm.IAlgorithm
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.util.copy
import ch.fhnw.afpars.util.zeros
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.imgproc.Imgproc

/**
 * Created by cansik on 16.10.16.
 */
class WatershedTest : IAlgorithm {
    @AlgorithmParameter(name = "Threshold", minValue = 0.0, maxValue = 255.0)
    var treshold = 10.0

    override fun run(image: AFImage, history: MutableList<AFImage>): AFImage {
        val mRgba = image.image
        val result = image.image.zeros()

        history.add(AFImage(mRgba.copy(), "Original"))
        //Core.subtract(mRgba, Scalar(treshold), result)

        Imgproc.cvtColor(mRgba, mRgba, Imgproc.COLOR_BGR2GRAY)
        mRgba.convertTo(mRgba, CvType.CV_8UC1)

        history.add(AFImage(mRgba.copy(), "BW"))

        var res = Core.minMaxLoc(mRgba)



        return AFImage(result)
    }

    override val name: String
        get() = "Watershed Test"
}