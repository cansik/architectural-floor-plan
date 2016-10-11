package ch.fhnw.afpars.io.reader

import ch.fhnw.afpars.model.AFImage
import java.nio.file.Path
import org.opencv.imgcodecs.Imgcodecs

/**
 * Created by Alexander on 11.10.2016.
 */
class AFImageReader : IDataReader{
    override fun read(path: Path): AFImage {
        val source = Imgcodecs.imread(path.toString())
        val afimg = AFImage(source)
        return afimg
    }
}