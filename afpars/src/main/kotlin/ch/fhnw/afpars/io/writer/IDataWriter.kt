package ch.fhnw.afpars.io.writer

import ch.fhnw.afpars.model.AFImage
import java.nio.file.Path

/**
 * Created by cansik on 07.10.16.
 */
interface IDataWriter {
    fun write(path: Path, image: AFImage)
}