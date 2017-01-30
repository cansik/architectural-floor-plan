package ch.fhnw.afpars.ui.control.editor.tools

import ch.fhnw.afpars.ui.control.editor.ImageEditor
import ch.fhnw.afpars.ui.control.editor.shapes.RectangleShape
import javafx.geometry.Dimension2D
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.input.MouseEvent

/**
 * Created by cansik on 25.01.17.
 */
class RectangleTool : BaseEditorTool() {
    override val cursor: Cursor
        get() = Cursor.CROSSHAIR

    var dragStart = Point2D.ZERO!!
    var current = RectangleShape()

    override fun onCanvasMousePressed(imageEditor: ImageEditor, event: MouseEvent) {
        dragStart = Point2D(event.x, event.y)
        current = RectangleShape(dragStart, Dimension2D(0.0, 0.0))
        imageEditor.activeLayer.shapes.add(current)
        imageEditor.redraw()
    }

    override fun onCanvasMouseDragged(imageEditor: ImageEditor, event: MouseEvent) {
        val points = sortPoints(dragStart, Point2D(event.x, event.y))
        current.size = Dimension2D(points.second.x - points.first.x, points.second.y - points.first.y)
        current.location = points.first
        imageEditor.redraw()
    }

    override fun onCanvasMouseReleased(imageEditor: ImageEditor, event: MouseEvent) {
        imageEditor.redraw()
    }
}