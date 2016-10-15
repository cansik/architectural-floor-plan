package ch.fhnw.afpars.algorithm

import ch.fhnw.afpars.model.AFImage

/**
 * Created by cansik on 07.10.16.
 */
interface IAlgorithm {
    fun run(image: AFImage): AFImage

    val name: String
        get
}