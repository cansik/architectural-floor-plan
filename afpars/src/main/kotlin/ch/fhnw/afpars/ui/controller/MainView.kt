package ch.fhnw.afpars.ui.controller

import ch.fhnw.afpars.algorithm.preprocessing.MorphologicalTransform
import ch.fhnw.afpars.algorithm.roomdetection.NikieRoomDetection
import ch.fhnw.afpars.algorithm.roomdetection.WatershedTest
import ch.fhnw.afpars.io.reader.AFImageReader
import ch.fhnw.afpars.ui.control.PreviewImageView
import ch.fhnw.afpars.util.toImage
import ch.fhnw.afpars.workflow.Workflow
import ch.fhnw.afpars.workflow.WorkflowEngine
import javafx.fxml.FXML
import javafx.stage.FileChooser
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

class MainView {
    @FXML
    var imageViewOriginal: PreviewImageView? = null

    @FXML
    var imageViewResult: PreviewImageView? = null

    val workflowEngine = WorkflowEngine()

    init {
        workflowEngine.finished += {
            println("algorithm workflowEngine finished!")
            // show result image with hough
            //val dest = drawHough(it.image)
            imageViewResult!!.newImage(it.image.toImage())
        }
    }

    fun testOpenCV_Clicked() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

        val fileChooser = FileChooser()
        fileChooser.title = "Open image"
        val file = fileChooser.showOpenDialog(null)
        if (file != null) {
            val source = AFImageReader().read(file.toPath())

            imageViewOriginal!!.newImage(source.image.toImage())

            val destination = source

            println("running algorithm workflow...")

            workflowEngine.run(Workflow(
                    arrayListOf(
                            WatershedTest()
                    ).toTypedArray()
            ), destination,
                    true)
        }
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

            val destination = source

            println("running algorithm workflow...")

            workflowEngine.run(Workflow(
                    arrayListOf(
                            MorphologicalTransform(),
                            NikieRoomDetection()
                    ).toTypedArray()
            ), destination,
                    true)

        }
    }

    /*
        //todo: create an algorithm for this processing
        Hough-Transformation mit einzeichnen der Linien in eine Mat()
     */
    fun drawHough(destination: Mat): Mat {
        //Canny Image
        //val dilation_size1 = 8
        //val element2 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(2.0 * dilation_size1 + 1.0, 2.0 * dilation_size1 + 1.0))
        //Imgproc.erode(destination,destination,element2)
        var canny = Mat()
        //Imgproc.blur(destination, canny, Size(3.0,3.0) );
        val threshlow = 1.0
        Imgproc.Canny(destination, canny, 50.0, 150.0);

        //HoughTransformation
        val lines = Mat()
        Imgproc.HoughLinesP(canny, lines, 1.0, Math.PI / 180, 0)

        //Weisse Mat()
        val dest = Mat(Size(destination.width().toDouble(), destination.height().toDouble()), 0)
        dest.setTo(Scalar(255.0, 255.0, 255.0))

        //Linien einzeichnen
        for (i in 0..lines.size().height.toInt() - 1) {
            //Diese Methode funktioniert noch nicht

            /*val rho = lines.get(i,0).get(0)
            val theta = lines.get(i,0).get(1)
            val a = Math.cos(theta)
            val b = Math.sin(rho)
            val x0 = a*rho
            val y0 = b*rho
            val pt1 = Point(Math.round(x0+1000*(-b)).toDouble(),Math.round(y0+100*(a)).toDouble())
            val pt2 = Point(Math.round(x0-1000*(-b)).toDouble(),Math.round(y0-100*(a)).toDouble())*/

            //Exakte Methode für Edge-Linien, hat evtl zu viele Linien
            val line = lines.get(i, 0)
            val pt1 = Point(line.get(0), line.get(1))
            val pt2 = Point(line.get(2), line.get(3))
            Imgproc.line(dest, pt1, pt2, Scalar(0.0, 0.0, 255.0), 3)
        }

        //Im moment wird eine Matrize mit allen Linien zurückgegeben.
        //Es könnte auch das ganze Bild zurückgegeben werden.
        return dest
    }
}
