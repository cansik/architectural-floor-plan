package ch.fhnw.afpars.ui.controller

import ch.fhnw.afpars.algorithm.areadetection.NikieRoomDetection
import ch.fhnw.afpars.algorithm.areadetection.RectangleRoomDetection
import ch.fhnw.afpars.algorithm.areadetection.TMDoorDetection
import ch.fhnw.afpars.algorithm.base.ScaleTest
import ch.fhnw.afpars.algorithm.objectdetection.CascadeClassifierDetector
import ch.fhnw.afpars.algorithm.objectdetection.OrbDetection
import ch.fhnw.afpars.algorithm.objectdetection.ShapeDistanceMatching
import ch.fhnw.afpars.algorithm.preprocessing.MorphologicalTransform
import ch.fhnw.afpars.io.reader.AFImageReader
import ch.fhnw.afpars.ui.control.PreviewImageView
import ch.fhnw.afpars.util.toImage
import ch.fhnw.afpars.workflow.Workflow
import ch.fhnw.afpars.workflow.WorkflowEngine
import javafx.fxml.FXML
import javafx.stage.FileChooser
import org.opencv.core.Core

class MainView {
    @FXML
    var imageViewOriginal: PreviewImageView? = null

    @FXML
    var imageViewResult: PreviewImageView? = null

    val workflowEngine = WorkflowEngine()

    init {
        workflowEngine.finished += {
            println("algorithm workflowEngine finished!")
            imageViewResult!!.newImage(it.image.toImage())
        }
    }

    fun testCCWithOrb() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

        val fileChooser = FileChooser()
        fileChooser.title = "Open image"
        val file = fileChooser.showOpenDialog(null)
        if (file != null) {
            val source = AFImageReader().read(file.toPath())

            val destination = source

            println("running cascade classifier with orb test...")

            workflowEngine.run(Workflow(
                    arrayListOf(
                            CascadeClassifierDetector(),
                            OrbDetection()
                    ).toTypedArray()
            ), destination,
                    true)
        }
    }

    fun testCascadeClassifer() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

        val fileChooser = FileChooser()
        fileChooser.title = "Open image"
        val file = fileChooser.showOpenDialog(null)
        if (file != null) {
            val source = AFImageReader().read(file.toPath())

            val destination = source

            println("running cascade classifier with shape distance test...")

            workflowEngine.run(Workflow(
                    arrayListOf(
                            CascadeClassifierDetector(),
                            ShapeDistanceMatching()
                    ).toTypedArray()
            ), destination,
                    true)
        }
    }

    fun testScaleClicked() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

        val fileChooser = FileChooser()
        fileChooser.title = "Open image"
        val file = fileChooser.showOpenDialog(null)
        if (file != null) {
            val source = AFImageReader().read(file.toPath())

            val destination = source

            println("running scale test...")

            workflowEngine.run(Workflow(
                    arrayListOf(
                            ScaleTest()
                    ).toTypedArray()
            ), destination,
                    true)
        }
    }

    fun testDoorDetectionClicked() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

        val fileChooser = FileChooser()
        fileChooser.title = "Open image"
        val file = fileChooser.showOpenDialog(null)
        if (file != null) {
            val source = AFImageReader().read(file.toPath())

            val destination = source

            println("running door test...")

            workflowEngine.run(Workflow(
                    arrayListOf(
                            //MorphologicalTransform(),
                            TMDoorDetection()
                    ).toTypedArray()
            ), destination,
                    true)
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
                            MorphologicalTransform(),
                            RectangleRoomDetection()
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
}
