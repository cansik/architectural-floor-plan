package ch.fhnw.afpars.ui.control.tools

import ch.fhnw.afpars.ui.control.ImageEditor
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent

/**
 * Created by cansik on 26.01.17.
 */
abstract class BaseEditorTool : IEditorTool {
    override fun onMouseClicked(editor: ImageEditor, event: MouseEvent) {}

    override fun setOnScroll(editor: ImageEditor, event: ScrollEvent) {}
}