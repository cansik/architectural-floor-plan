package ch.fhnw.afpars.model

import ch.fhnw.afpars.util.copy
import org.opencv.core.Mat

/**
 * Created by cansik on 06.10.16.
 */
class AFImage : Cloneable {
    var name: String
    var image: Mat

    constructor(image: Mat) : this(image, "AF Image") {
    }

    constructor(image: Mat, name: String) {
        this.image = image
        this.name = name
    }

    public override fun clone(): AFImage {
        val img = AFImage(image.copy())
        return img
    }
}