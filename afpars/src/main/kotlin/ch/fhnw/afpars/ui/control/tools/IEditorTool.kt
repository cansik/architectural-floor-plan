package ch.fhnw.afpars.ui.control.tools

import ch.fhnw.afpars.ui.control.ImageEditor
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent

/**
 * Created by cansik on 25.01.17.
 */
interface IEditorTool {

    fun onMouseClicked(editor: ImageEditor, event: MouseEvent)
    fun setOnScroll(editor: ImageEditor, event: ScrollEvent)
}