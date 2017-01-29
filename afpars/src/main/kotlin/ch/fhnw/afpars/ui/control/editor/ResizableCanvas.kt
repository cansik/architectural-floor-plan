package ch.fhnw.afpars.ui.control.editor

import javafx.scene.canvas.Canvas

/**
 * Created by cansik on 29.01.17.
 */
class ResizableCanvas(width: Double, height: Double) : Canvas(width, height) {
    override fun isResizable(): Boolean {
        return true
    }

    override fun resize(width: Double, height: Double) {
        super.resize(width, height)
        this.width = width
        this.height = height
    }
}