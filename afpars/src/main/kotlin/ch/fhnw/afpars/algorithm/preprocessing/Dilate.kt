package ch.fhnw.afpars.algorithm.preprocessing

import ch.fhnw.afpars.model.AFImage
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class Dilate:IPreprocessingAlgorithm{
    val dilationSize:Int

    constructor(dilationSize: Int){
        this.dilationSize = dilationSize
    }

    override fun run(imageAF: AFImage): AFImage {
        return run(imageAF,dilationSize)
    }

    fun run(imageAF: AFImage, dilationSize: Int): AFImage {
        val element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(2.0 * dilationSize + 1.0, 2.0 * dilationSize + 1.0))
        val result = AFImage(Mat())
        Imgproc.dilate(imageAF.image, result.image, element)
        return result
    }
}