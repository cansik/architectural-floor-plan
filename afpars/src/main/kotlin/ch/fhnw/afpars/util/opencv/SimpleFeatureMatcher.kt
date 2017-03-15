package ch.fhnw.afpars.util.opencv

import ch.fhnw.afpars.util.to8UC3
import org.opencv.core.Mat
import org.opencv.core.MatOfDMatch
import org.opencv.core.MatOfKeyPoint
import org.opencv.features2d.DescriptorExtractor
import org.opencv.features2d.DescriptorMatcher
import org.opencv.features2d.FeatureDetector
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil.close
import java.io.OutputStreamWriter
import java.io.FileOutputStream
import java.io.File
import org.bytedeco.javacpp.Loader.getCacheDir





/**
 * Created by cansik on 03.01.17.
 */
class SimpleFeatureMatcher(detectorType: Int = FeatureDetector.ORB,
                           extractorType: Int = DescriptorExtractor.ORB,
                           matcherType: Int = DescriptorMatcher.BRUTEFORCE_HAMMING) {

    private val detector = FeatureDetector.create(detectorType)
    private val extractor = DescriptorExtractor.create(extractorType)
    private val matcher = DescriptorMatcher.create(matcherType)

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