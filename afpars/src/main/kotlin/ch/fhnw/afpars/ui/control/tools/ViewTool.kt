package ch.fhnw.afpars.ui.control.tools

import ch.fhnw.afpars.ui.control.ImageEditor
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent

/**
 * Created by cansik on 25.01.17.
 */
class ViewTool : BaseEditorTool() {
    val scaleSpeed = 1.0 / 50.0

    override fun onMouseClicked(editor: ImageEditor, event: MouseEvent) {
        println("Mouse Clicked: ${event.x} | ${event.y}")
    }

    override fun setOnScroll(editor: ImageEditor, event: ScrollEvent) {
        print("ZoomScale: ${editor.zoomScale}")
        println("\tDelta: ${event.deltaY * scaleSpeed}")



        editor.zoomScale += event.deltaY * scaleSpeed
        editor.resize()
    }
}