package ch.fhnw.afpars.ui.control.editor.tools

import ch.fhnw.afpars.ui.control.editor.ImageEditor
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent

/**
 * Created by cansik on 26.01.17.
 */
abstract class BaseEditorTool : IEditorTool {
    override fun onCanvasMouseClicked(imageEditor: ImageEditor, event: MouseEvent) {}
    override fun onCanvasMousePressed(imageEditor: ImageEditor, event: MouseEvent) {}
    override fun onCanvasMouseReleased(imageEditor: ImageEditor, event: MouseEvent) {}
    override fun onCanvasMouseDragged(imageEditor: ImageEditor, event: MouseEvent) {}
    override fun onCanvasMouseMoved(imageEditor: ImageEditor, event: MouseEvent) {}
    override fun onCanvasScroll(imageEditor: ImageEditor, event: ScrollEvent) {}
    override fun onCanvasKeyPressed(imageEditor: ImageEditor, event: KeyEvent) {}

    override fun onEditorMouseClicked(imageEditor: ImageEditor, event: MouseEvent) {}
    override fun onEditorMousePressed(imageEditor: ImageEditor, event: MouseEvent) {}
    override fun onEditorMouseReleased(imageEditor: ImageEditor, event: MouseEvent) {}
    override fun onEditorMouseDragged(imageEditor: ImageEditor, event: MouseEvent) {}
    override fun onEditorMouseMoved(imageEditor: ImageEditor, event: MouseEvent) {}
    override fun onEditorScroll(imageEditor: ImageEditor, event: ScrollEvent) {}
    override fun onEditorKeyPressed(imageEditor: ImageEditor, event: KeyEvent) {}

    override val cursor: Cursor
        get() = Cursor.DEFAULT

    internal fun sortPoints(a: Point2D, b: Point2D): Pair<Point2D, Point2D> {
        val x1 = if (a.x < b.x) a.x else b.x
        val x2 = if (a.x > b.x) a.x else b.x

        val y1 = if (a.y < b.y) a.y else b.y
        val y2 = if (a.y > b.y) a.y else b.y

        return Pair(Point2D(x1, y1), Point2D(x2, y2))
    }
}