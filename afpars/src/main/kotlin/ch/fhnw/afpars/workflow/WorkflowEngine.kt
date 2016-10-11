package ch.fhnw.afpars.workflow

import ch.fhnw.afpars.model.AFImage
import org.opencv.core.Mat

/**
 * Created by cansik on 07.10.16.
 */
class WorkflowEngine {

    fun run(workflow: Workflow, afImage: AFImage):AFImage {
        var image = afImage
        for(alg in workflow.algorithms){
            image = alg.run(image)
        }
        return image
    }
}