package ch.fhnw.afpars.algorithm.preprocessing

import ch.fhnw.afpars.algorithm.AlgorithmParameter
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.util.dilate
import ch.fhnw.afpars.util.erode
import ch.fhnw.afpars.util.threshold

/**
 * Created by cansik on 13.10.16.
 */
class MorphologicalTransform() : IPreprocessingAlgorithm {
    @AlgorithmParameter(name = "Threshold", minValue = 0.0, maxValue = 255.0)
    var treshold = 128.0

    @AlgorithmParameter(name = "Opening")
    var openingSize = 8

    @AlgorithmParameter(name = "Closing")
    var closingSize = 34

    constructor(treshold: Double, openingSize: Int, closingSize: Int) : this() {
        this.treshold = treshold
        this.openingSize = openingSize
        this.closingSize = closingSize
    }

    override fun run(image: AFImage): AFImage {
        /*
        var img = Dilate(openingSize).run(image)
        img = Erode(openingSize).run(img)
        img = Erode(closingSize).run(img)
        return Dilate(closingSize).run(img)
        */

        return image
                .threshold(treshold)
                .dilate(openingSize)
                .erode(openingSize)
                .erode(closingSize)
                .dilate(closingSize)
    }
}