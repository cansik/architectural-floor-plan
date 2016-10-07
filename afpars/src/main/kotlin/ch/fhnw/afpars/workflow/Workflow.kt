package ch.fhnw.afpars.workflow

import ch.fhnw.afpars.algorithm.IAlgorithm

/**
 * Created by cansik on 07.10.16.
 */
class Workflow {

    val algorithms: Array<out IAlgorithm>

    constructor(vararg algorithms: IAlgorithm) {
        this.algorithms = algorithms
    }
}