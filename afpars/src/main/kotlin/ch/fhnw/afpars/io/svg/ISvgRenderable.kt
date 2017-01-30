package ch.fhnw.afpars.io.svg

import org.jfree.graphics2d.svg.SVGGraphics2D

/**
 * Created by cansik on 29.01.17.
 */
interface ISvgRenderable {
    fun renderToSvg(g: SVGGraphics2D)
}