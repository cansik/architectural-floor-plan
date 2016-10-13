package ch.fhnw.afpars.workflow

import ch.fhnw.afpars.algorithm.IAlgorithm

/**
 * Created by cansik on 07.10.16.
 */
class Workflow {

    val algorithms: Array<out IAlgorithm>

    constructor(algorithms: Array<out IAlgorithm>) {
        this.algorithms = algorithms
    }
}