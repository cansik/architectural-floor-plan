package ch.fhnw.afpars.ui.controller

import ch.fhnw.afpars.util.toImage
import javafx.scene.image.ImageView
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs
import kotlin.properties.Delegates
import javafx.fxml.FXML
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

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
        val source = Imgcodecs.imread("data/A_N1.png")
        Imgproc.threshold(source,source, 253.0,255.0,0)

        imageViewOriginal!!.image = source.toImage()

        var destination = Mat(source.rows(), source.cols(), source.type())

        destination = source

        val dilation_size1 = 6
        val erosion_size = 20
        val dilation_size = 20

        val element2 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(2.0 * dilation_size1 + 1.0, 2.0 * dilation_size1 + 1.0))
        Imgproc.dilate(source, destination, element2)

        destination = source

        val element3 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(2.0 * dilation_size1 + 1.0, 2.0 * dilation_size1 + 1.0))
        Imgproc.erode(source, destination, element3)
        destination = source

        val element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(2.0 * erosion_size + 1.0, 2.0 * erosion_size + 1.0))
        Imgproc.erode(source, destination, element)

        destination = source

        val element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(2.0 * dilation_size + 1.0, 2.0 * dilation_size + 1.0))
        Imgproc.dilate(source, destination, element1)
        imageViewResult!!.image = destination.toImage()
    }
}
