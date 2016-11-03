package ch.fhnw.afpars.algorithm.base

import ch.fhnw.afpars.algorithm.AlgorithmParameter
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.util.resize
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

/**
 * Created by cansik on 03.11.16.
 */
class ScaleTest : IBaseAlgorithm {
    @AlgorithmParameter(name = "Erosion Size")
    val erosionSize: Double = 10.0

    override fun run(image: AFImage, history: MutableList<AFImage>): AFImage {

        val img = image.image
        val images = mutableListOf(img)
        images.add(img.resize(img.width() / 2, 0))
        images.add(img.resize(img.width() / 4, 0))
        images.add(img.resize(img.width() / 8, 0))
        images.add(img.resize(img.width() / 16, 0))

        val element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(2.0 * erosionSize + 1.0, 2.0 * erosionSize + 1.0))

        // calculate threshold
        for (i in images) {
            Imgproc.erode(i, i, element)
            history.add(AFImage(i, "${i.size()}"))
        }

        return image
    }

    override val name: String
        get() = "Scale Test"

}