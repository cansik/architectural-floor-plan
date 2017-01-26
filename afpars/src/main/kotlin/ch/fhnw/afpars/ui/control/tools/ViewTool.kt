package ch.fhnw.afpars.ui.control.tools

import ch.fhnw.afpars.ui.control.ImageEditor
import javafx.geometry.Point2D
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent

/**
 * Created by cansik on 25.01.17.
 */
class ViewTool : BaseEditorTool() {
    val scaleSpeed = 1.0 / 50.0

    var dragStart = Point2D.ZERO!!

    override fun onEditorMousePressed(imageEditor: ImageEditor, event: MouseEvent) {
        dragStart = Point2D(event.x, event.y)
    }

    override fun onEditorMouseDragged(imageEditor: ImageEditor, event: MouseEvent) {
        val point = Point2D(event.x, event.y)
        val delta = dragStart.subtract(point)

        imageEditor.canvasTransformation = delta.multiply(-1.0)
        imageEditor.resize()

        dragStart = point
    }

    override fun onEditorScroll(imageEditor: ImageEditor, event: ScrollEvent) {
        imageEditor.zoomScale += event.deltaY * scaleSpeed
        imageEditor.resize()
    }
}