package ch.fhnw.afpars.ui.controller

import ch.fhnw.afpars.algorithm.preprocessing.Canny
import ch.fhnw.afpars.algorithm.preprocessing.Dilate
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.util.toImage
import javafx.fxml.FXML
import javafx.scene.control.Slider
import javafx.scene.image.ImageView
import org.opencv.core.Mat
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue

/**
 * Created by Alexander on 12.10.2016.
 */
class DilateView {

    @FXML
    var dilation: Slider? = Slider()

    @FXML
    var showimage: ImageView? = ImageView()

    var imageAF:AFImage = AFImage(Mat())

    constructor(){
        dilation!!.min = 0.0
        dilation!!.max = 100.0
        dilation!!.minorTickCount = 1
        showimage = ImageView()
        /*val changelistener = ChangeListener<Number>(){
            override fun changed(ov: ObservableValue<Number>, on: Number,nn:Number){
                showimage!!.image=(Dilate(nn.toInt()).run(imageAF,nn.toInt())).image.toImage()
        }
        }*/
        /*dilation!!.valueProperty().addListener { (object : ChangeListener<Number>{
            override fun changed(observable: ObservableValue<out Number>?, oldValue: Number?, newValue: Number?) {
                showimage!!.image=(Dilate(newValue!!.toInt()).run(imageAF,newValue!!.toInt())).image.toImage()
            }
        })
        }*/
        dilation!!.valueProperty().addListener { observable, oldvalue, newvalue -> showimage!!.image=(Dilate(newvalue.toInt()).run(imageAF,newvalue.toInt())).image.toImage()}
    }

    fun setImage(image:AFImage){
        this.imageAF = image
        showimage!!.image = imageAF.image.toImage()
    }

    fun setDilationSize(dilationSize:Int){
        dilation!!.value = dilationSize.toDouble()
    }

}