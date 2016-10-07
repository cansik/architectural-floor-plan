package ch.fhnw.afpars.model

import org.opencv.core.Mat

/**
 * Created by cansik on 06.10.16.
 */
class AFImage {
    val image: Mat

    constructor(image: Mat) {
        this.image = image
    }
}