package ch.fhnw.afpars.workflow

import ch.fhnw.afpars.algorithm.IAlgorithm
import ch.fhnw.afpars.algorithm.preprocessing.Dilate
import ch.fhnw.afpars.algorithm.preprocessing.Erode

/**
 * Created by cansik on 07.10.16.
 */
class Workflow {
    
    val algorithms: Array<out IAlgorithm>

    constructor(algorithms: Array<out IAlgorithm>) {
        this.algorithms = algorithms
    }
}