package ch.fhnw.afpars.ui.control.editor.shapes

import javafx.geometry.Dimension2D
import javafx.geometry.Point2D

/**
 * Created by cansik on 29.01.17.
 */
class CircleShape(center: Point2D, radius: Double) : OvalShape(center, Dimension2D(radius * 2.0, radius * 2.0))