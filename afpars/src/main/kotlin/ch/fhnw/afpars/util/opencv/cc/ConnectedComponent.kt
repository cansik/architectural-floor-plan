package ch.fhnw.afpars.util.opencv.cc

import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

/**
 * Created by cansik on 05.02.17.
 */
class ConnectedComponent {
    val label: Int

    val location: Point
    val size: Size
    val area: Double

    val centroid: Point

    constructor(label: Int, rectRow: Mat, centroidRow: Mat) {
        this.label = label

        // copy data
        area = rectRow.get(0, Imgproc.CC_STAT_AREA)[0]
        location = Point(rectRow.get(0, Imgproc.CC_STAT_LEFT)[0], rectRow.get(0, Imgproc.CC_STAT_TOP)[0])
        size = Size(rectRow.get(0, Imgproc.CC_STAT_WIDTH)[0], rectRow.get(0, Imgproc.CC_STAT_HEIGHT)[0])

        // create centroid
        val centroidData = DoubleArray(2)
        centroidRow.get(0, 0, centroidData)
        centroid = Point(centroidData[0], centroidData[1])
    }
}