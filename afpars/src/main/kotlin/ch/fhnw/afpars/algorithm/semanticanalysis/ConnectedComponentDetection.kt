package ch.fhnw.afpars.algorithm.semanticanalysis

import ch.fhnw.afpars.algorithm.AlgorithmParameter
import ch.fhnw.afpars.algorithm.IAlgorithm
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.util.*
import org.opencv.core.Core
import org.opencv.core.Scalar

/**
 * Created by cansik on 13.02.17.
 */
class ConnectedComponentDetection : IAlgorithm {

    @AlgorithmParameter(name = "Threshold", minValue = 0.0, maxValue = 255.0)
    var treshold = 128.0

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

        //mask.threshold(1.0)

        // find contours


        history.add(AFImage(gray, "Gray"))
        history.add(AFImage(mask, "Mask"))

        // cleanup
        nativeComponents.release()

        return image
    }
}