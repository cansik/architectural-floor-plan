package ch.fhnw.afpars.ui.control.tools

import ch.fhnw.afpars.ui.control.ImageEditor
import javafx.scene.input.MouseEvent

/**
 * Created by cansik on 25.01.17.
 */
class ViewTool : IEditorTool {
    override fun onMouseClicked(editor: ImageEditor, event: MouseEvent) {
        println("Mouse Clicked: ${event.x} | ${event.y}")
    }
}