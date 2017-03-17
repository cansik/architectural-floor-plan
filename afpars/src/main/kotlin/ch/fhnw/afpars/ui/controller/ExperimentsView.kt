package ch.fhnw.afpars.ui.controller

import ch.fhnw.afpars.algorithm.base.ScaleTest
import ch.fhnw.afpars.algorithm.informationsegmentation.MorphologicalTransform
import ch.fhnw.afpars.algorithm.informationsegmentation.RectangleRoomDetection
import ch.fhnw.afpars.algorithm.semanticanalysis.ConnectedComponentDetection
import ch.fhnw.afpars.algorithm.semanticanalysis.GapClosingAlgorithm
import ch.fhnw.afpars.algorithm.semanticanalysis.NikieRoomDetection
import ch.fhnw.afpars.algorithm.structuralanalysis.CascadeClassifierDetector
import ch.fhnw.afpars.algorithm.structuralanalysis.OrbDetection
import ch.fhnw.afpars.algorithm.structuralanalysis.ShapeDistanceMatching
import ch.fhnw.afpars.algorithm.structuralanalysis.TMDoorDetection
import ch.fhnw.afpars.io.reader.AFImageReader
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.ui.control.PreviewImageView
import ch.fhnw.afpars.util.opencv.combinePoints
import ch.fhnw.afpars.util.opencv.sparsePoints
import ch.fhnw.afpars.util.toImage
import ch.fhnw.afpars.workflow.Workflow
import ch.fhnw.afpars.workflow.WorkflowEngine
import javafx.fxml.FXML
import javafx.stage.FileChooser
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

class ExperimentsView {
    @FXML
    lateinit var imageViewOriginal: PreviewImageView

    @FXML
    lateinit var imageViewResult: PreviewImageView

    val workflowEngine = WorkflowEngine()

    init {
        workflowEngine.finished += {
            println("algorithm workflowEngine finished!")
            imageViewResult.newImage(it.image.toImage())
        }
    }


    fun testSparsePoint() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

        val image = Mat.zeros(500, 500, CvType.CV_8UC3)
        val points = mutableListOf<Point>()

        for (i in 0..50) {
            val p = Point(Math.random() * image.width(), Math.random() * image.height())
            Imgproc.circle(image, p, 2, Scalar(0.0, 255.0, 0.0))
            Imgproc.circle(image, p, 30, Scalar(255.0, 100.0, 0.0))
            points.add(p)
        }

        // make sparse cloud
        val sparsePoints = points.sparsePoints(30.0).combinePoints()

        for (p in sparsePoints)
            Imgproc.circle(image, p, 3, Scalar(0.0, 0.0, 255.0))

        imageViewOriginal.newImage(image.toImage())
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
                            CascadeClassifierDetector("cascade-files/cascade_thicken.xml", AFImage.DOOR_ATTRIBUTE_NAME),
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

            imageViewOriginal.newImage(source.image.toImage())

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

            imageViewOriginal.newImage(source.image.toImage())

            val destination = source

            println("running algorithm workflow...")

            workflowEngine.run(Workflow(
                    arrayListOf(

                            CascadeClassifierDetector(AFImage.DOOR_CASCADE, AFImage.DOOR_ATTRIBUTE_NAME),
                            MorphologicalTransform(),
                            GapClosingAlgorithm(),
                            NikieRoomDetection(),
                            ConnectedComponentDetection()
                    ).toTypedArray()
            ), destination,
                    true)

        }
    }
}
