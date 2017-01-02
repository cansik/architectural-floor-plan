package ch.fhnw.afpars.util

import org.opencv.core.CvType
import org.opencv.core.Mat

/**
 * Created by cansik on 03.11.16.
 */

fun Mat.convert(type: Int): Mat {
    val dest = this.zeros()
    this.convertTo(dest, type)
    return dest
}

fun Mat.to8U(): Mat {
    return this.convert(CvType.CV_8U)
}

fun Mat.to8UC1(): Mat {
    return this.convert(CvType.CV_8UC1)
}

fun Mat.to8UC3(): Mat {
    return this.convert(CvType.CV_8UC3)
}

fun Mat.to32S(): Mat {
    return this.convert(CvType.CV_32S)
}

fun Mat.to32SC1(): Mat {
    return this.convert(CvType.CV_32SC1)
}

fun Mat.to32SC3(): Mat {
    return this.convert(CvType.CV_32SC3)
}

fun Mat.to32FC2(): Mat {
    return this.convert(CvType.CV_32FC2)
}