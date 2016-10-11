package ch.fhnw.afpars.util

import ch.fhnw.afpars.algorithm.IAlgorithm
import ch.fhnw.afpars.algorithm.preprocessing.Dilate
import ch.fhnw.afpars.algorithm.preprocessing.Erode
import javafx.scene.image.Image
import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.io.ByteArrayInputStream

fun Mat.toImage(): Image {
    val byteMat = MatOfByte()
    Imgcodecs.imencode(".bmp", this, byteMat)
    return Image(ByteArrayInputStream(byteMat.toArray()))
}

/*
Hough-Transformation mit einzeichnen der Linien in eine Mat()
 */
fun drawHough(destination:Mat):Mat{
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
    val dest = Mat(Size(destination.width().toDouble(),destination.height().toDouble()),0)
    dest.setTo(Scalar(255.0,255.0,255.0))

    //Linien einzeichnen
    for(i in 0..lines.size().height.toInt()-1){
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
        val line = lines.get(i,0)
        val pt1 = Point(line.get(0),line.get(1))
        val pt2 = Point(line.get(2), line.get(3))
        Imgproc.line(dest,pt1,pt2, Scalar(0.0,0.0,255.0),3)
    }

    //Im moment wird eine Matrize mit allen Linien zurückgegeben.
    //Es könnte auch das ganze Bild zurückgegeben werden.
    return dest
}

fun standardAlg():Array<out IAlgorithm>{
    val algorithms:MutableList<IAlgorithm> = arrayListOf()
    algorithms.add(Dilate(8))
    algorithms.add(Erode(8))
    algorithms.add(Erode(25))
    algorithms.add(Dilate(34))
    return algorithms.toTypedArray()
}