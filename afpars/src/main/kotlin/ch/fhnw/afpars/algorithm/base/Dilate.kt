package ch.fhnw.afpars.algorithm.base

import ch.fhnw.afpars.algorithm.AlgorithmParameter
import ch.fhnw.afpars.model.AFImage
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class Dilate : IBaseAlgorithm {

    @AlgorithmParameter(name = "Dilation Size")
    var dilationSize: Int

    constructor(dilationSize: Int) {
        this.dilationSize = dilationSize
    }

    override fun run(image: AFImage): AFImage {
        System.out.println(dilationSize.toDouble())
        return run(image, dilationSize)
    }

    fun run(imageAF: AFImage, dilationSize: Int): AFImage {
        val element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(2.0 * dilationSize + 1.0, 2.0 * dilationSize + 1.0))
        val result = AFImage(Mat())
        Imgproc.dilate(imageAF.image, result.image, element)
        return result
    }
}