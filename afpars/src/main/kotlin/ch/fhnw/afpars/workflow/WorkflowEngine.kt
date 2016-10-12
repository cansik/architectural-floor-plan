package ch.fhnw.afpars.workflow

import ch.fhnw.afpars.event.Event
import ch.fhnw.afpars.model.AFImage
import kotlin.concurrent.thread

/**
 * Created by cansik on 07.10.16.
 */
class WorkflowEngine {
    val finished = Event<AFImage>()

    fun run(workflow: Workflow, afImage: AFImage) {
        thread {
            var image = afImage
            for (alg in workflow.algorithms) {
                image = alg.run(image)
            }
            finished(image)
        }
    }
}