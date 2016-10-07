package ch.fhnw.afpars.io.reader

import ch.fhnw.afpars.model.AFImage
import java.nio.file.Path

/**
 * Created by cansik on 07.10.16.
 */
interface IDataReader {
    fun read(path: Path): AFImage
}