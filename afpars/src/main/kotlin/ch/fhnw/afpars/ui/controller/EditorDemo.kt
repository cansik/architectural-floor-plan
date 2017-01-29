package ch.fhnw.afpars.ui.controller

import ch.fhnw.afpars.ui.control.editor.ImageEditor
import ch.fhnw.afpars.ui.control.editor.tools.LineTool
import ch.fhnw.afpars.ui.control.editor.tools.OvalTool
import ch.fhnw.afpars.ui.control.editor.tools.RectangleTool
import ch.fhnw.afpars.ui.control.editor.tools.ViewTool
import javafx.event.ActionEvent
import javafx.fxml.FXML
import org.opencv.core.Core

/**
 * Created by cansik on 15.01.17.
 */
class EditorDemo {
    @FXML
    var editor: ImageEditor? = null

    init {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
    }

    fun viewToolClicked(e: ActionEvent) {
        editor!!.activeTool = ViewTool()
    }

    fun ellipseToolClicked(e: ActionEvent) {
        editor!!.activeTool = OvalTool()
    }

    fun lineToolClicked(e: ActionEvent) {
        editor!!.activeTool = LineTool()
    }

    fun rectangleToolClicked(e: ActionEvent) {
        editor!!.activeTool = RectangleTool()
    }
}