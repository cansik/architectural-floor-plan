package ch.fhnw.afpars.ui.controller

import ch.fhnw.afpars.io.reader.AFImageReader
import ch.fhnw.afpars.util.drawHough
import ch.fhnw.afpars.util.toImage
import javafx.fxml.FXML
import javafx.scene.image.ImageView
import javafx.stage.FileChooser
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class MainView {
    @FXML
    var imageViewOriginal: ImageView? = null

    @FXML
    var imageViewResult: ImageView? = null

    fun testOpenCV_Clicked() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
        val mat = Mat.eye(3, 3, CvType.CV_8UC1)
        System.out.println("mat = " + mat.dump())
    }

    fun testImage_Clicked() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
        //val source = Imgcodecs.imread("data/A_N1.png")
        //Imgproc.threshold(source, source, 253.0, 255.0, 0)

        val fileChooser = FileChooser()
        fileChooser.title = "Open image"
        val file = fileChooser.showOpenDialog(null)
        if(file != null){
        val source = AFImageReader().read(file.toPath())

        imageViewOriginal!!.image = source.image.toImage()

        var destination = source.image

        val dilation_size1 = 8
        val erosion_size = 25
        val dilation_size = 34

        val element2 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(2.0 * dilation_size1 + 1.0, 2.0 * dilation_size1 + 1.0))
        Imgproc.dilate(destination, destination, element2)

        val element3 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(2.0 * dilation_size1 + 1.0, 2.0 * dilation_size1 + 1.0))
        Imgproc.erode(destination, destination, element3)

        val element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(2.0 * erosion_size + 1.0, 2.0 * erosion_size + 1.0))
        Imgproc.erode(destination, destination, element)


        val element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(2.0 * dilation_size + 1.0, 2.0 * dilation_size + 1.0))
        Imgproc.dilate(destination, destination, element1)

        //Hough-Transformation
        val dest = drawHough(destination)

        imageViewResult!!.image = dest.toImage()}
    }


}
