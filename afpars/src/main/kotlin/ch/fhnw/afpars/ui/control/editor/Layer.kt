package ch.fhnw.afpars.ui.control.editor

import ch.fhnw.afpars.ui.control.editor.shapes.BaseShape

/**
 * Created by cansik on 28.01.17.
 */
class Layer(var name: String = "Layer") {
    var visible = true

    val shapes = mutableListOf<BaseShape>()

    override fun toString(): String {
        return "$name (${shapes.size})"
    }
}