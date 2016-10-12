package ch.fhnw.afpars.algorithm.preprocessing

import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.ui.controller.DilateView
import ch.fhnw.afpars.util.toImage
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Slider
import javafx.scene.image.ImageView
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class Dilate:IPreprocessingAlgorithm{
    var dilationSize:Int

    constructor(dilationSize: Int){
        this.dilationSize = dilationSize
    }

    override fun run(imageAF: AFImage): AFImage {
        System.out.println(dilationSize.toDouble())

        val fxmlLoader = FXMLLoader(javaClass.classLoader.getResource("view/LocalView.fxml"))
        val root1:Parent = fxmlLoader.load()
        val controller = fxmlLoader.getController<DilateView>()
        controller.setImage(imageAF)
        controller.setDilationSize(dilationSize)
        val stage = Stage()
        stage.setTitle("Test")
        stage.setScene(Scene(root1))
        stage.showAndWait()
        return run(imageAF,dilationSize)
    }

    fun run(imageAF: AFImage, dilationSize: Int): AFImage {
        val element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(2.0 * dilationSize + 1.0, 2.0 * dilationSize + 1.0))
        val result = AFImage(Mat())
        Imgproc.dilate(imageAF.image, result.image, element)
        return result
    }
}