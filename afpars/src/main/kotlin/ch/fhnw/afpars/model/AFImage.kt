package ch.fhnw.afpars.model

import org.opencv.core.Mat

/**
 * Created by cansik on 06.10.16.
 */
class AFImage : Cloneable {
    var image: Mat

    constructor(image: Mat) {
        this.image = image
    }

    public override fun clone(): AFImage {
        val img = AFImage(image.clone())
        return img
    }
}