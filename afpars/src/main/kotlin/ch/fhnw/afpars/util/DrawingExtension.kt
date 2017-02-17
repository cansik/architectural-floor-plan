package ch.fhnw.afpars.util

import javafx.geometry.Dimension2D
import javafx.geometry.Point2D
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import org.opencv.core.*

/**
 * Created by cansik on 29.01.17.
 */
fun Paint.toAwtColor(): java.awt.Color {
    if (this is Color)
        return java.awt.Color((this.red * 255).toInt(), (this.red * 255).toInt(), (this.red * 255).toInt())
    else
        return java.awt.Color(255, 0, 0)
}

fun Paint.toCvScalar(): Scalar {
    if (this is Color)
        return Scalar(this.red * 255, this.green * 255, this.blue * 255)
    else
        return Scalar(255.0, 0.0, 0.0)
}

fun Point2D.toCvPoint(): Point {
    return Point(this.x, this.y)
}

fun Dimension2D.toCvSize(): Size {
    return Size(this.width, this.height)
}