package ch.fhnw.afpars.algorithm.semanticanalysis

import ch.fhnw.afpars.algorithm.AlgorithmParameter
import ch.fhnw.afpars.algorithm.IAlgorithm
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.util.*
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

/**
 * Created by cansik on 13.02.17.
 */
class ConnectedComponentDetection : IAlgorithm {

    @AlgorithmParameter(name = "Threshold", minValue = 0.0, maxValue = 255.0)
    var treshold = 128.0

    @AlgorithmParameter(name = "Border Approx", minValue = 0.0, maxValue = 255.0)
    var borderApprox = 10.0

    override val name: String
        get() = "Connected Component Detection"

    override fun run(image: AFImage, history: MutableList<AFImage>): AFImage {
        // watershed image! 128 => Background, 0 => Borders, 200 => foreground
        val gray = image.image.copy().to8U()
        gray.threshold(treshold)

        val nativeComponents = gray.connectedComponentsWithStats()
        val components = nativeComponents.getConnectedComponents()

        println("found ${components.size - 1} components!")

        // grab all areas
        val mask = gray.zeros()

        components.filter { it.label != 0 }.forEach {
            val labeledMask = nativeComponents.labeled.getRegionMask(it.label)

            labeledMask.replaceColor(Scalar(255.0), Scalar(55.0 + (200 / (components.size - 1) * it.label)))
            Core.add(mask, labeledMask, mask)
            labeledMask.release()
        }

        // create color image
        val colorMap = gray.zeros(CvType.CV_8UC3)
        Imgproc.applyColorMap(mask, colorMap, 2)

        // find contours
        val contours = mask.findContours()
        contours.drawContours(colorMap, color = Scalar(255.0, 255.0, 255.0))

        println("found ${contours.contours.size} contours")

        // remove contours which touch the image border
        contours.contours.removeAll { it.isOnBorder(gray, borderApprox) }

        // create output
        

        history.add(AFImage(gray, "Gray"))
        history.add(AFImage(mask, "Mask"))
        history.add(AFImage(colorMap, "ColorMap"))

        // cleanup
        nativeComponents.release()
        contours.release()

        return image
    }
}