package ch.fhnw.afpars.ui.control.tools

import ch.fhnw.afpars.ui.control.ImageEditor
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
    override fun onCanvasScroll(imageEditor: ImageEditor, event: ScrollEvent) {}
    override fun onCanvasKeyPressed(imageEditor: ImageEditor, event: KeyEvent) {}

    override fun onEditorMouseClicked(imageEditor: ImageEditor, event: MouseEvent) {}
    override fun onEditorMousePressed(imageEditor: ImageEditor, event: MouseEvent) {}
    override fun onEditorMouseReleased(imageEditor: ImageEditor, event: MouseEvent) {}
    override fun onEditorMouseDragged(imageEditor: ImageEditor, event: MouseEvent) {}
    override fun onEditorScroll(imageEditor: ImageEditor, event: ScrollEvent) {}
    override fun onEditorKeyPressed(imageEditor: ImageEditor, event: KeyEvent) {}
}