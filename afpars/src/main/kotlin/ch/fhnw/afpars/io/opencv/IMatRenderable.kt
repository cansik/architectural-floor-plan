package ch.fhnw.afpars.io.opencv

import org.opencv.core.Mat

/**
 * Created by cansik on 29.01.17.
 */
interface IMatRenderable {
    fun renderToMat(m: Mat)
}