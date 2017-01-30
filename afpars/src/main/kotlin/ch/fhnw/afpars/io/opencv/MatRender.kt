package ch.fhnw.afpars.io.opencv

import org.opencv.core.Mat

/**
 * Created by cansik on 29.01.17.
 */
object MatRender {
    fun render(m: Mat, shapes: List<IMatRenderable>): Mat {
        shapes.forEach { it.renderToMat(m) }
        return m
    }
}