package ch.fhnw.afpars.util.opencv

import org.opencv.core.Mat
import org.opencv.core.MatOfKeyPoint

/**
 * Created by cansik on 03.01.17.
 */
class FeatureExtractionResult(val keypoints: MatOfKeyPoint, val descriptors: Mat)