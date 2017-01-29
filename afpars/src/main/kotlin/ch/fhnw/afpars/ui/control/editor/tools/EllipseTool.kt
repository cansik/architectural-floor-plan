package ch.fhnw.afpars.ui.control.editor.tools

import javafx.scene.Cursor

/**
 * Created by cansik on 29.01.17.
 */
class EllipseTool : BaseEditorTool() {
    override val cursor: Cursor
        get() = Cursor.CROSSHAIR
}