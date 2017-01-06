package ch.fhnw.afpars.io.reader

import ch.fhnw.afpars.model.AFImage
import java.nio.file.Path
import org.opencv.imgcodecs.Imgcodecs

/**
 * Created by Alexander on 11.10.2016.
 */
class AFImageReader : IDataReader{
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