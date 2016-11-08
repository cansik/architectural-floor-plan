package ch.fhnw.afpars.algorithm.areadetection

import ch.fhnw.afpars.model.AFImage

/**
 * Created by cansik on 08.11.16.
 */
class VectorDoorDetection : IAreaDetectionAlgorithm {
    override fun run(image: AFImage, history: MutableList<AFImage>): AFImage {
        return image
    }

    override val name: String
        get() = "Vector Door Detection"
}