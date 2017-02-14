package ch.fhnw.afpars.algorithm.semanticanalysis

import ch.fhnw.afpars.algorithm.AlgorithmParameter
import ch.fhnw.afpars.algorithm.IAlgorithm
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.util.connectedComponentsWithStats
import ch.fhnw.afpars.util.copy
import ch.fhnw.afpars.util.threshold
import ch.fhnw.afpars.util.to8U

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

        println("found ${components.size} components!")

        history.add(AFImage(gray, "Gray"))

        // cleanup
        nativeComponents.release()

        return image
    }
}