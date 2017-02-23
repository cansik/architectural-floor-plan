package ch.fhnw.afpars.algorithm.semanticanalysis

import ch.fhnw.afpars.algorithm.AlgorithmParameter
import ch.fhnw.afpars.algorithm.IAlgorithm
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.model.RoomPolygonShape
import ch.fhnw.afpars.ui.control.editor.shapes.PolygonShape
import ch.fhnw.afpars.util.*
import javafx.geometry.Point2D
import javafx.scene.paint.Color
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

/**
 * Created by cansik on 13.02.17.
 */
class ConnectedComponentDetection : IAlgorithm {
    companion object
    {
        @JvmStatic
        val ROOM_LAYER_NAME = "rooms"
    }

    @AlgorithmParameter(name = "Threshold", minValue = 0.0, maxValue = 255.0)
    var treshold = 128.0

    @AlgorithmParameter(name = "Border Approx", minValue = 0.0, maxValue = 200.0)
    var borderApprox = 2.0

    @AlgorithmParameter(name = "Min Room Size %", helpText = "Minimum room size in percentage relation to the biggest room.",
            minValue = 0.0, maxValue = 100.0, majorTick = 1.0)
    var minRoomSize = 5.0

    override val name: String
        get() = "Connected Component Detection"

    override fun run(image: AFImage, history: MutableList<AFImage>): AFImage {

        val borderApproxImage = image.image.clone()

        val gray = image.image.copy().to8U()
        Imgproc.cvtColor(gray, gray, Imgproc.COLOR_BGR2GRAY)
        gray.threshold(treshold)

        val nativeComponents = gray.connectedComponentsWithStats()
        val components = nativeComponents.getConnectedComponents()

        println("found ${components.size - 1} components!")

        // grab all areas
        val mask = gray.zeros()

        components.filter { it.label != 0 }.forEach {
            val labeledMask = nativeComponents.labeled.getRegionMask(it.label)

            labeledMask.replaceColor(Scalar(255.0), Scalar(55.0 + (200 / (components.size - 1) * it.label)))
            Core.add(mask, labeledMask, mask)
            labeledMask.release()
        }

        // create color image
        val colorMap = gray.zeros(CvType.CV_8UC3)

        Imgproc.applyColorMap(mask, colorMap, 2)

        // find contours
        val contours = mask.findContours()
        contours.drawContours(colorMap, color = Scalar(255.0, 255.0, 255.0))

        println("found ${contours.contours.size} contours")

        // remove contours which touch the image border
        contours.contours.removeAll { it.isOnBorder(gray, borderApprox) }

        // remove minimum contours
        val roomPolygons =  contours.contours.map { RoomPolygonShape(it, it.nativeContour.toArray().map { Point2D(it.x, it.y) }.toMutableList())}
        val biggestContourArea = roomPolygons.map { it.area() }.max() ?: 0.0

        contours.contours.removeAll ( roomPolygons.filter { it.area() * (100.0 / biggestContourArea) < minRoomSize }.map { it.contour } )

        // create output image with border approx
        contours.drawContours(borderApproxImage, color = Scalar(0.0, 133.0, 242.0), thickness = -1)

        // add output shapes
        image.addLayer(ROOM_LAYER_NAME, *contours.contours
                .map { c ->
                    val s = RoomPolygonShape(c, c.nativeContour.toArray().map { Point2D(it.x, it.y) }.toMutableList())
                    s.fill = Color(0.0, 1.0, 1.0, 0.5)
                    s.stroke = Color(0.0, 1.0, 1.0, 1.0)
                    s
                }.toTypedArray())

        history.add(AFImage(gray, "Gray"))
        history.add(AFImage(mask, "Mask"))
        history.add(AFImage(colorMap, "ColorMap"))
        history.add(AFImage(borderApproxImage, "Border Approx"))

        // cleanup
        nativeComponents.release()
        contours.release()

        return image
    }
}