package ch.fhnw.afpars.util

import javafx.scene.paint.Color

/**
 * Created by cansik on 29.01.17.
 */
fun Color.toAWT(): java.awt.Color {
    return java.awt.Color((this.red * 255).toInt(), (this.red * 255).toInt(), (this.red * 255).toInt())
}