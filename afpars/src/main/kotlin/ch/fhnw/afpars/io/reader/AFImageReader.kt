package ch.fhnw.afpars.io.reader

import ch.fhnw.afpars.model.AFImage
import org.opencv.imgcodecs.Imgcodecs
import java.nio.file.Path

/**
 * Created by Alexander on 11.10.2016.
 */
class AFImageReader : IDataReader {
    companion object {
        val ORIGINAL_IMAGE = "originalimage"
    }

    override fun read(path: Path): AFImage {
        val source = Imgcodecs.imread(path.toString())
        val afimg = AFImage(source)
        afimg.attributes.put(AFImageReader.ORIGINAL_IMAGE, afimg.image.clone())
        return afimg
    }
}