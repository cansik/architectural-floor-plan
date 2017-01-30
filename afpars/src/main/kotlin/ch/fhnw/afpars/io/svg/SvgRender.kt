package ch.fhnw.afpars.io.svg

import org.jfree.graphics2d.svg.SVGGraphics2D


/**
 * Created by cansik on 29.01.17.
 */
object SvgRender {
    fun render(width: Int, height: Int, shapes: List<ISvgRenderable>): SVGGraphics2D {
        val g = SVGGraphics2D(width, height)
        shapes.forEach { it.renderToSvg(g) }
        return g
    }
}