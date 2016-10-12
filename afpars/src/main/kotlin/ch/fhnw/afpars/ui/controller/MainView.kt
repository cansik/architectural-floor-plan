package ch.fhnw.afpars.ui.controller

import ch.fhnw.afpars.io.reader.AFImageReader
import ch.fhnw.afpars.ui.control.PreviewImageView
import ch.fhnw.afpars.util.drawHough
import ch.fhnw.afpars.util.standardAlg
import ch.fhnw.afpars.util.toImage
import ch.fhnw.afpars.workflow.Workflow
import ch.fhnw.afpars.workflow.WorkflowEngine
import javafx.fxml.FXML
import javafx.stage.FileChooser
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat

class MainView {
    @FXML
    var imageViewOriginal: PreviewImageView? = null

    @FXML
    var imageViewResult: PreviewImageView? = null

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
        if (file != null) {
            val source = AFImageReader().read(file.toPath())

            imageViewOriginal!!.newImage(source.image.toImage())

            var destination = source

            val workflow = WorkflowEngine()
            destination = workflow.run(Workflow(standardAlg()), destination)

            //Hough-Transformation
            val dest = drawHough(destination.image)

            imageViewResult!!.newImage(dest.toImage())
        }
    }


}
