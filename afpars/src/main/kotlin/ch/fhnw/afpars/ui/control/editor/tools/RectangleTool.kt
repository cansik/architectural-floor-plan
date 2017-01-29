package ch.fhnw.afpars.ui.control.editor.tools

import javafx.scene.Cursor

/**
 * Created by cansik on 25.01.17.
 */
class RectangleTool : BaseEditorTool() {
    override val cursor: Cursor
        get() = Cursor.CROSSHAIR
}