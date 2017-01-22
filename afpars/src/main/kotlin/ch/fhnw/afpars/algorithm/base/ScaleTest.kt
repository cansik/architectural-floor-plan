package ch.fhnw.afpars.algorithm.base

import ch.fhnw.afpars.algorithm.AlgorithmParameter
import ch.fhnw.afpars.algorithm.IAlgorithm
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.util.copy
import ch.fhnw.afpars.util.resize
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

/**
 * Created by cansik on 03.11.16.
 */
class ScaleTest : IAlgorithm {
    @AlgorithmParameter(name = "Erosion Size")
    val erosionSize: Double = 10.0

    override fun run(image: AFImage, history: MutableList<AFImage>): AFImage {

        val img = image.image.copy()
        val images = mutableListOf(img)
        images.add(img.resize(img.width() / 2, 0))
        images.add(img.resize(img.width() / 4, 0))
        images.add(img.resize(img.width() / 8, 0))
        images.add(img.resize(img.width() / 16, 0))

        val originalSize = img.width()

        // calculate threshold
        for (i in images) {
            val relativeSize = i.width() / originalSize.toDouble()
            val relativeErosion = relativeSize * erosionSize
            val element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
                    Size(2.0 * relativeErosion + 1.0, 2.0 * relativeErosion + 1.0))

            Imgproc.erode(i, i, element)
            history.add(AFImage(i, "${i.size()}"))

            println("${i.size()}:\tsize: $relativeSize\terosion: $relativeErosion")
        }

        return image
    }

    override val name: String
        get() = "Scale Test"

}