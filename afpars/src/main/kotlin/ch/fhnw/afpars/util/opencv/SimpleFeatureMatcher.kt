package ch.fhnw.afpars.util.opencv

import ch.fhnw.afpars.util.to8UC3
import org.bytedeco.javacpp.Loader.getCacheDir
import org.opencv.core.Mat
import org.opencv.core.MatOfDMatch
import org.opencv.core.MatOfKeyPoint
import org.opencv.features2d.DescriptorMatcher
import org.opencv.features2d.DescriptorMatcher.BRUTEFORCE_HAMMING
import org.opencv.features2d.Feature2D
import org.opencv.features2d.ORB
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter


/**
 * Created by cansik on 03.01.17.
 */
class SimpleFeatureMatcher(private val detector: Feature2D = ORB.create(),
                           private val extractor: Feature2D = ORB.create(),
                           private val matcher: DescriptorMatcher = DescriptorMatcher.create(BRUTEFORCE_HAMMING)) {

    init {
        // set arguments
        val outputDir = getCacheDir()
        val outputFile = File.createTempFile("orbDetectorParams", ".YAML", outputDir)
        writeToFile(outputFile, "%YAML:1.0\nscaleFactor: 1.2\nnLevels: 8\nfirstLevel: 0 \nedgeThreshold: 31\npatchSize: 31\nWTA_K: 2\nscoreType: 1\nnFeatures: 500\n")
        detector.read(outputFile.path)

        println("detector read parameter")
    }

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

    private fun writeToFile(file: File, data: String) {
        val stream = FileOutputStream(file)
        val outputStreamWriter = OutputStreamWriter(stream)
        outputStreamWriter.write(data)
        outputStreamWriter.close()
        stream.close()
    }
}