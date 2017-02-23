package ch.fhnw.afpars.algorithm.informationsegmentation

import ch.fhnw.afpars.algorithm.AlgorithmParameter
import ch.fhnw.afpars.algorithm.IAlgorithm
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.util.dilate
import ch.fhnw.afpars.util.erode
import ch.fhnw.afpars.util.threshold
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

/**
 * Created by cansik on 13.10.16.
 */
class MorphologicalTransform() : IAlgorithm {
    companion object {
        val MORPH = "MorphTransform"
    }

    override val name: String
        get() = "Morphological Transform"

    @AlgorithmParameter(name = "Threshold", minValue = 0.0, maxValue = 255.0)
    var treshold = 200.0

    @AlgorithmParameter(name = "Opening", minValue = 0.0, maxValue = 20.0)
    var openingSize = 9

    @AlgorithmParameter(name = "Closing")
    var closingSize = 12

    constructor(threshold: Double, openingSize: Int, closingSize: Int) : this() {
        this.treshold = threshold
        this.openingSize = openingSize
        this.closingSize = closingSize
    }

    override fun run(image: AFImage, history: MutableList<AFImage>): AFImage {
        val img = image.clone()
        image.attributes.remove(MORPH)

        // create greyscale of colored image
        Imgproc.cvtColor(img.image, img.image, Imgproc.COLOR_BGR2GRAY)
        Imgproc.cvtColor(img.image, img.image, Imgproc.COLOR_GRAY2BGR)

        img.image.threshold(treshold)

        // opening
        img.image.dilate(openingSize)
        img.image.erode(openingSize)

        // closing
        img.image.erode(closingSize)
        img.image.dilate(closingSize)

        image.attributes.put(MORPH,img.image)
        history.add(img)
        return img
    }
}