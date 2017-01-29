package ch.fhnw.afpars.ui.control.editor.tools

import ch.fhnw.afpars.ui.control.editor.ImageEditor
import javafx.scene.Cursor
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent

/**
 * Created by cansik on 25.01.17.
 */
interface IEditorTool {
    fun onCanvasMouseClicked(imageEditor: ImageEditor, event: MouseEvent)
    fun onCanvasMousePressed(imageEditor: ImageEditor, event: MouseEvent)
    fun onCanvasMouseReleased(imageEditor: ImageEditor, event: MouseEvent)
    fun onCanvasMouseDragged(imageEditor: ImageEditor, event: MouseEvent)
    fun onCanvasMouseMoved(imageEditor: ImageEditor, event: MouseEvent)
    fun onCanvasScroll(imageEditor: ImageEditor, event: ScrollEvent)
    fun onCanvasKeyPressed(imageEditor: ImageEditor, event: KeyEvent)

    fun onEditorMouseClicked(imageEditor: ImageEditor, event: MouseEvent)
    fun onEditorMousePressed(imageEditor: ImageEditor, event: MouseEvent)
    fun onEditorMouseReleased(imageEditor: ImageEditor, event: MouseEvent)
    fun onEditorMouseDragged(imageEditor: ImageEditor, event: MouseEvent)
    fun onEditorMouseMoved(imageEditor: ImageEditor, event: MouseEvent)
    fun onEditorScroll(imageEditor: ImageEditor, event: ScrollEvent)
    fun onEditorKeyPressed(imageEditor: ImageEditor, event: KeyEvent)

    val cursor: Cursor
        get
}