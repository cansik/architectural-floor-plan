package ch.fhnw.afpars.ui.controller

import ch.fhnw.afpars.util.toImage
import javafx.scene.image.ImageView
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs
import kotlin.properties.Delegates
import javafx.fxml.FXML

/**
 * Created by cansik on 29.09.16.
 */
class MainView {
    @FXML
    var imageViewOriginal: ImageView? = null

    @FXML
    var imageViewResult:ImageView? = null

    fun testOpenCV_Clicked()
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
        val mat = Mat.eye(3, 3, CvType.CV_8UC1)
        System.out.println("mat = " + mat.dump())
    }

    fun testImage_Clicked()
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
        val image = Imgcodecs.imread("data/A_N1.png")
        imageViewOriginal!!.image = image!!.toImage()
    }
}
