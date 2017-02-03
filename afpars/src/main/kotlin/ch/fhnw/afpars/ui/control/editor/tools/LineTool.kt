package ch.fhnw.afpars.ui.control.editor.tools

import ch.fhnw.afpars.ui.control.editor.ImageEditor
import ch.fhnw.afpars.ui.control.editor.shapes.LineShape
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.paint.Paint

/**
 * Created by cansik on 25.01.17.
 */
open class LineTool : BaseEditorTool() {
    var dragStart = Point2D.ZERO!!
    var current = LineShape()

    var defaultStroke: Paint = Color.BLACK!!
    var defaultStrokeWeight = 1.0

    override val cursor: Cursor
        get() = Cursor.CROSSHAIR

    override fun onCanvasMousePressed(imageEditor: ImageEditor, event: MouseEvent) {
        dragStart = Point2D(event.x, event.y)
        current = LineShape(dragStart, dragStart)
        current.stroke = defaultStroke
        current.strokeWeight = defaultStrokeWeight

        imageEditor.addShape(current)
        imageEditor.redraw()
    }

    override fun onCanvasMouseDragged(imageEditor: ImageEditor, event: MouseEvent) {
        current.point2 = Point2D(event.x, event.y)
        imageEditor.redraw()
    }

    override fun onCanvasMouseReleased(imageEditor: ImageEditor, event: MouseEvent) {
        imageEditor.redraw()
    }
}