package ch.fhnw.afpars.ui.controller

import ch.fhnw.afpars.ui.control.editor.ImageEditor
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
}