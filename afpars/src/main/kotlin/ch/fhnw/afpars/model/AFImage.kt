package ch.fhnw.afpars.model

import ch.fhnw.afpars.util.copy
import org.opencv.core.Mat

/**
 * Created by cansik on 06.10.16.
 */
class AFImage : Cloneable {
    var name: String
    var image: Mat
    val attributes:MutableMap<String,Mat>


    constructor(image: Mat, name: String = "AFImage", attributes: MutableMap<String,Mat> = mutableMapOf()) {
        this.image = image
        this.name = name
        this.attributes = attributes
    }

    public override fun clone(): AFImage {
        val img = AFImage(image.copy(),name,this.attributes)
        return img
    }
}