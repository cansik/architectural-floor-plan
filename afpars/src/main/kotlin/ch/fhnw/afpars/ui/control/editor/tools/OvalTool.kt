package ch.fhnw.afpars.ui.control.editor.tools

import ch.fhnw.afpars.ui.control.editor.ImageEditor
import ch.fhnw.afpars.ui.control.editor.shapes.OvalShape
import javafx.geometry.Dimension2D
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.input.MouseEvent

/**
 * Created by cansik on 29.01.17.
 */
class OvalTool : BaseEditorTool() {
    override val cursor: Cursor
        get() = Cursor.CROSSHAIR

    var dragStart = Point2D.ZERO!!
    var current = OvalShape()

    override fun onCanvasMousePressed(imageEditor: ImageEditor, event: MouseEvent) {
        dragStart = Point2D(event.x, event.y)
        current = OvalShape(dragStart, Dimension2D(0.0, 0.0))
        imageEditor.activeLayer.shapes.add(current)
        imageEditor.redraw()
    }

    override fun onCanvasMouseDragged(imageEditor: ImageEditor, event: MouseEvent) {
        current.size = Dimension2D(Math.abs(dragStart.x - event.x), Math.abs(dragStart.y - event.y))
        imageEditor.redraw()
    }

    override fun onCanvasMouseReleased(imageEditor: ImageEditor, event: MouseEvent) {
        imageEditor.redraw()
    }
}