package ch.fhnw.afpars.util.opencv

import ch.fhnw.afpars.util.to8UC3
import org.opencv.core.Mat
import org.opencv.core.MatOfDMatch
import org.opencv.core.MatOfKeyPoint
import org.opencv.features2d.DescriptorExtractor
import org.opencv.features2d.DescriptorMatcher
import org.opencv.features2d.FeatureDetector

/**
 * Created by cansik on 03.01.17.
 */
class SimpleFeatureMatcher(detectorType: Int = FeatureDetector.ORB,
                           extractorType: Int = DescriptorExtractor.ORB,
                           matcherType: Int = DescriptorMatcher.BRUTEFORCE_HAMMING) {

    private val detector = FeatureDetector.create(detectorType)
    private val extractor = DescriptorExtractor.create(extractorType)
    private val matcher = DescriptorMatcher.create(matcherType)

    fun extract(image: Mat): FeatureExtractionResult {
        val keypoints = MatOfKeyPoint()
        val descriptors = Mat()

        val img = image.to8UC3()
        detector.detect(img, keypoints)
        extractor.compute(img, keypoints, descriptors)

        return FeatureExtractionResult(keypoints, descriptors)
    }

    fun match(image1: Mat, image2: Mat): MatOfDMatch {
        return match(extract(image1).descriptors, extract(image2).descriptors)
    }

    fun match(descriptors1: MatOfKeyPoint, descriptors2: MatOfKeyPoint): MatOfDMatch {
        val matches = MatOfDMatch()
        matcher.match(descriptors1, descriptors2, matches)
        return matches
    }
}