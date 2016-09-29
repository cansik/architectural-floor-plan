/**
 * Created by cansik on 29.09.16.
 */
package ch.fhnw.afpars

import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat

class Main {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            val mat = Mat.eye(3, 3, CvType.CV_8UC1);
            System.out.println("mat = " + mat.dump());
        }
    }
}